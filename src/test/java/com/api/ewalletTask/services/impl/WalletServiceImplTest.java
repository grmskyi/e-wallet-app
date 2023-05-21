package com.api.ewalletTask.services.impl;

import com.api.ewalletTask.Exceptions.*;
import com.api.ewalletTask.models.Customer;
import com.api.ewalletTask.models.Wallet;
import com.api.ewalletTask.repositories.CustomerRepository;
import com.api.ewalletTask.repositories.TransactionRepository;
import com.api.ewalletTask.repositories.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class WalletServiceImplTest {
    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrUpdateWallet() {
        // Create a wallet
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(100.0);

        // Mock the wallet repository save method
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // Invoke the createOrUpdateWallet method
        Wallet createdWallet = walletService.createOrUpdateWallet(wallet);

        // Verify the wallet repository save method is called with the correct wallet
        verify(walletRepository).save(wallet);

        // Verify that the created wallet is returned
        assertEquals(wallet, createdWallet);
    }

    @Test
    void testDeleteWallet() throws WalletNotFoundException {
        // Mock the wallet repository findById method
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(new Wallet()));

        // Invoke the deleteWallet method
        String result = walletService.deleteWallet(1L);

        // Verify the wallet repository findById method is called with the correct id
        verify(walletRepository).findById(1L);

        // Verify the wallet repository delete method is called
        verify(walletRepository).delete(any(Wallet.class));

        // Verify that the expected result is returned
        assertEquals("Wallet is deleted", result);
    }

    @Test
    void testDeleteWallet_WalletNotFoundException() {
        // Mock the wallet repository findById method to return an empty optional
        when(walletRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verify that a WalletNotFoundException is thrown when invoking the deleteWallet method
        assertThrows(WalletNotFoundException.class, () -> walletService.deleteWallet(1L));

        // Verify the wallet repository findById method is called with the correct id
        verify(walletRepository).findById(1L);

        // Verify that the wallet repository delete method is not called
        verify(walletRepository, never()).delete(any(Wallet.class));
    }



    @Test
    void testTransferToAnotherCustomer_WhenSenderCustomerNotFound() {
        // Mock the customer repository findById method to return an empty optional
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verify that the SenderNotFoundException is thrown
        assertThrows(SenderNotFoundException.class, () ->
                walletService.transferToAnotherCustomer(1L, 1L, 2L, 2L, 50.0));

        // Verify that the customer repository findById method is called with the correct id
        verify(customerRepository).findById(1L);
    }



}