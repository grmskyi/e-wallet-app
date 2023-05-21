package com.api.ewalletTask.services.impl;

import com.api.ewalletTask.Exceptions.*;
import com.api.ewalletTask.models.Customer;
import com.api.ewalletTask.models.Transaction;
import com.api.ewalletTask.models.Wallet;
import com.api.ewalletTask.repositories.CustomerRepository;
import com.api.ewalletTask.repositories.TransactionRepository;
import com.api.ewalletTask.repositories.WalletRepository;
import com.api.ewalletTask.services.CustomerService;
import com.api.ewalletTask.services.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {

    static final String CUSTOMER_NOT_FOUND_ERROR = "Customer not found.";
    private static final String WALLET_NOT_FOUND_ERROR = "Wallet not found.";
    private static final String LEGAL = "legal";
    private static final String TRANSFER = "Transfer";

    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    private final CustomerService customerService;

    @Override
    public Wallet createOrUpdateWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    @Override
    public String deleteWallet(Long id) throws WalletNotFoundException {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for id: " + id));

        walletRepository.delete(wallet);
        return "Wallet is deleted";
    }

    @Override
    public Wallet transferToAnotherCustomer(Long senderCustomerId, Long senderWalletId, Long receiverCustomerId, Long receiverWalletId, Double amount) {
        Optional<Customer> optionalSenderCustomer = customerRepository.findById(senderCustomerId);
        Customer senderCustomer = optionalSenderCustomer.orElseThrow(() -> new SenderNotFoundException("Sender customer not found."));

        customerService.handleBlockedCustomer(senderCustomer.getId());

        Optional<Wallet> optionalSenderWallet = senderCustomer.getWallets().stream()
                .filter(wallet -> wallet.getId().equals(senderWalletId))
                .findFirst();
        Wallet senderWallet = optionalSenderWallet.orElseThrow(() -> new SenderWalletNotFoundException("Sender wallet not found."));

        if (senderWallet.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient balance in the sender wallet.");
        }

        Optional<Customer> optionalReceiverCustomer = customerRepository.findById(receiverCustomerId);
        Customer receiverCustomer = optionalReceiverCustomer.orElseThrow(() -> new RecieverCustomerNotFoundException("Receiver customer not found."));

        Optional<Wallet> optionalReceiverWallet = receiverCustomer.getWallets().stream()
                .filter(wallet -> wallet.getId().equals(receiverWalletId))
                .findFirst();
        Wallet receiverWallet = optionalReceiverWallet.orElseThrow(() -> new RecieverWalletNotFoundException("Receiver wallet not found."));

        updateWalletsAndCreateTransaction(senderWallet, receiverWallet, amount);
        checkAndUpdateTransactionCount(senderCustomer);

        if (Boolean.TRUE.equals(senderCustomer.getBlocked())) {
            customerService.blockCustomerForWeek(senderCustomerId);
        }

        return senderWallet;
    }
    private void updateWalletsAndCreateTransaction(Wallet senderWallet, Wallet receiverWallet, Double amount) {
        Double senderWalletBalance = senderWallet.getBalance();
        Double receiverWalletBalance = receiverWallet.getBalance();

        senderWallet.setBalance(senderWalletBalance - amount);
        receiverWallet.setBalance(receiverWalletBalance + amount);

        double totalAmountForSenderWallet = transactionRepository.getTotalAmountForWalletExcludingDeposits(senderWallet.getId());

        Transaction transaction = new Transaction();
        transaction.setType(TRANSFER);
        transaction.setTimestamp(new Date());
        transaction.setAmount(amount);
        transaction.setPostBalance(senderWallet.getBalance());
        transaction.setDescription("transfer to " + receiverWallet.getCustomer().getName());
        transaction.setWallet(senderWallet);

        setTransactionTag(transaction, totalAmountForSenderWallet, amount);

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
        transactionRepository.save(transaction);
    }

    private void setTransactionTag(Transaction transaction, double totalAmountForSenderWallet, double amount) {
        if (amount > 2000) {
            throw new TransactionLimitException("Single transaction limit exceeded.");
        }

        if (!transaction.getType().equals("Deposit") && totalAmountForSenderWallet + amount > 10000) {
            transaction.setTag("suspicious");
        } else {
            transaction.setTag(LEGAL);
        }
    }

    private void checkAndUpdateTransactionCount(Customer customer) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime oneHourAgo = currentDateTime.minusHours(1);

        List<Transaction> transactions = transactionRepository.findByWalletCustomer(customer);

        long recentTransactionCount = transactions.stream()
                .filter(transaction -> transaction.getTimestamp().toInstant().isAfter(oneHourAgo.atZone(ZoneId.systemDefault()).toInstant()))
                .count();

        if (Boolean.TRUE.equals(customer.getBlocked())) {
            recentTransactionCount = 0;
        }
        long recentTransferCount = transactions.stream()
                .filter(transaction -> transaction.getTimestamp().toInstant().isAfter(oneHourAgo.atZone(ZoneId.systemDefault()).toInstant()))
                .filter(transaction -> transaction.getType().equals(TRANSFER))
                .count();

        if (recentTransactionCount > 10) {
            if (!Boolean.TRUE.equals(customer.getUnblockRequested()) && (customer.getTimeLeftToUnblock() == null || customer.getTimeLeftToUnblock().toMinutes() <= 0)) {
                customer.setBlocked(true);
                customerRepository.save(customer);
            }
        } else {
            customer.setBlocked(false); // Clear the blocked status
            customerRepository.save(customer);
        }



        if (recentTransactionCount > 5 && recentTransferCount > 0) {
            transactions.stream()
                    .filter(transaction -> transaction.getTimestamp().toInstant().isAfter(oneHourAgo.atZone(ZoneId.systemDefault()).toInstant()))
                    .filter(transaction -> TRANSFER.equals(transaction.getType()))
                    .skip(5)
                    .forEach(transaction -> transaction.setTag("suspicious"));

            transactionRepository.saveAll(transactions);
        }
    }

    @Override
    public Wallet depositToWallet(Long walletId, Double amount) {
        Optional<Wallet> optionalWallet = walletRepository.findById(walletId);
        Wallet wallet = optionalWallet.orElseThrow(() -> new WalletNotFoundException(WALLET_NOT_FOUND_ERROR));

        // Increase the balance of the wallet by the deposit amount
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        // Create a deposit transaction
        depositTransaction(wallet, amount);

        return wallet;
    }

    private void depositTransaction(Wallet wallet, Double amount) {
        Transaction transaction = new Transaction();
        transaction.setType("Deposit");
        transaction.setTimestamp(new Date());
        transaction.setAmount(amount);
        transaction.setPostBalance(wallet.getBalance());
        transaction.setWallet(wallet);
        transaction.setTag(LEGAL);

        transaction.setDescription("Deposit to " + wallet.getCustomer().getName());

        transactionRepository.save(transaction);
    }

    @Override
    public Wallet withdrawFromAccount(Long walletId, Long customerId, Double amount, boolean disableLimit) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        Customer customer = optionalCustomer.orElseThrow(() -> new CustomerNotFoundException(CUSTOMER_NOT_FOUND_ERROR));

        Optional<Wallet> optionalWallet = customer.getWallets().stream()
                .filter(wallet -> wallet.getId().equals(walletId))
                .findFirst();
        Wallet wallet = optionalWallet.orElseThrow(() -> new WalletNotFoundException(WALLET_NOT_FOUND_ERROR));

        if (wallet.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient balance in the wallet.");
        }

        if (!disableLimit) {
            double remainingDailyLimit = getRemainingDailyLimit(walletId);

            if (amount > remainingDailyLimit) {
                throw new WithdrawAmountException("Withdraw amount exceeds the remaining daily limit of " + remainingDailyLimit + " EUR.");
            }
        }

        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);


        withdrawTransaction(wallet, amount);

        return wallet;
    }

    private double getRemainingDailyLimit(Long walletId) {
        LocalDate currentDate = LocalDate.now();
        LocalTime startTime = LocalTime.MIN;
        LocalDateTime startOfDay = LocalDateTime.of(currentDate, startTime);
        Date startOfDayDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());

        double dailyWithdrawTotal = transactionRepository.getDailyWithdrawTotal(walletId, startOfDayDate);
        return 5000 - dailyWithdrawTotal;
    }

    private void withdrawTransaction(Wallet wallet, Double amount) {
        Transaction transaction = new Transaction();
        transaction.setType("Withdrawal");
        transaction.setTimestamp(new Date());
        transaction.setAmount(amount);
        transaction.setPostBalance(wallet.getBalance());
        transaction.setDescription("Withdraw from " + wallet.getAccountName());
        transaction.setWallet(wallet);
        transaction.setTag(LEGAL);

        transactionRepository.save(transaction);
    }

}

