package com.api.ewalletTask.services.impl;

import com.api.ewalletTask.Exceptions.BaseException;
import com.api.ewalletTask.models.Customer;
import com.api.ewalletTask.models.Wallet;
import com.api.ewalletTask.repositories.CustomerRepository;
import com.api.ewalletTask.repositories.TransactionRepository;
import com.api.ewalletTask.repositories.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

class WalletServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;


    @Mock
    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void withdrawFromAccount_InsufficientBalance_ThrowsException() {
        Long walletId = 1L;
        Long customerId = 1L;
        Double amount = 100.0;
        boolean disableLimit = false;

        Customer customer = new Customer();
        Wallet wallet = new Wallet();
        wallet.setBalance(50.0);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        Assertions.assertThrows(BaseException.class, () -> walletService.withdrawFromAccount(walletId, customerId, amount, disableLimit));
    }

    @Test
    void withdrawFromAccount_ExceedsDailyLimit_ThrowsException() {

        Long walletId = 1L;
        Long customerId = 1L;
        Double amount = 6000.0;
        boolean disableLimit = false;

        Customer customer = new Customer();
        Wallet wallet = new Wallet();
        wallet.setBalance(10000.0);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.getDailyWithdrawTotal(eq(walletId), any())).thenReturn(3000.0);


        Assertions.assertThrows(BaseException.class, () -> walletService.withdrawFromAccount(walletId, customerId, amount, disableLimit));
    }

    @Test
    void getCustomerOrThrow_CustomerExists_ReturnsCustomer() {
        Long customerId = 1L;
        Customer customer = new Customer();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Customer result = walletService.getCustomerOrThrow(Optional.of(customer));

        assertEquals(customer, result);
    }

    @Test
    void getCustomerOrThrow_CustomerDoesNotExist_ThrowsException() {
        Optional<Customer> optionalCustomer = Optional.empty();
        Assertions.assertThrows(BaseException.class, () -> walletService.getCustomerOrThrow(optionalCustomer));
    }

    @Test
    void getWalletOrThrow_WalletExists_ReturnsWallet() {

        Long walletId = 1L;
        Wallet wallet = new Wallet();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWalletOrThrow(Optional.of(wallet));

        assertEquals(wallet, result);
    }

    @Test
    void getWalletOrThrow_WalletDoesNotExist_ThrowsException() {
        Optional<Wallet> optionalWallet = Optional.empty();
        Assertions.assertThrows(BaseException.class, () -> walletService.getWalletOrThrow(optionalWallet));
    }

}