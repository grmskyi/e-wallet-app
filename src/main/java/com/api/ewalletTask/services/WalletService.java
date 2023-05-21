package com.api.ewalletTask.services;


import com.api.ewalletTask.models.Wallet;

public interface WalletService {

    Wallet createOrUpdateWallet(Wallet wallet);

    String deleteWallet(Long id);

    Wallet transferToAnotherCustomer(Long senderCustomerId, Long senderWalletId, Long receiverCustomerId, Long receiverWalletId, Double amount);

    Wallet withdrawFromAccount(Long walletId, Long customerId, Double amount, boolean disableLimit);

    Wallet depositToWallet(Long walletId, Double amount);
}
