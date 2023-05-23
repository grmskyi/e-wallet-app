package com.api.ewalletTask.services;


import com.api.ewalletTask.dtos.WalletDTO;

public interface WalletService {

    WalletDTO createOrUpdateWallet(WalletDTO walletDTO);

    String deleteWallet(Long id);

    WalletDTO transferToAnotherCustomer(Long senderCustomerId, Long senderWalletId, Long receiverCustomerId, Long receiverWalletId, Double amount);

    WalletDTO withdrawFromAccount(Long walletId, Long customerId, Double amount, boolean disableLimit);

    WalletDTO depositToWallet(Long walletId, Double amount);
}
