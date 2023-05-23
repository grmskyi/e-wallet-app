package com.api.ewalletTask.controllers;


import com.api.ewalletTask.Exceptions.BaseException;
import com.api.ewalletTask.dtos.CustomerDTO;
import com.api.ewalletTask.dtos.WalletDTO;
import com.api.ewalletTask.services.CustomerService;
import com.api.ewalletTask.services.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/e-wallet")
public class EWalletController {
    private final WalletService walletService;
    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerDTO> createNewAccount(@Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO createdCustomer = customerService.createOrUpdateCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @DeleteMapping("/customer/{id}")
    public ResponseEntity<String> deleteCustomerAccount(@PathVariable Long id) {
        String message = customerService.deleteCustomer(id);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/{customerId}/request-unblocking")
    public ResponseEntity<String> requestUnblocking(@PathVariable Long customerId) {
        try {
            customerService.requestUnblocking(customerId);
            return ResponseEntity.ok("Unblocking request submitted successfully.");
        } catch (BaseException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<WalletDTO> createWallet(@Valid @RequestBody WalletDTO walletDTO) {
        WalletDTO createdWallet = walletService.createOrUpdateWallet(walletDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWallet);
    }

    @DeleteMapping("/wallet/{id}")
    public ResponseEntity<String> deleteWallet(@PathVariable Long id) {
        String message = walletService.deleteWallet(id);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/transfer-to-another-customer")
    public ResponseEntity<WalletDTO> transferToAnotherCustomer(@RequestParam Long senderCustomerId,
                                                               @RequestParam Long senderWalletId,
                                                               @RequestParam Long receiverCustomerId,
                                                               @RequestParam Long receiverWalletId,
                                                               @RequestParam Double amount) {
        WalletDTO transferredWallet = walletService.transferToAnotherCustomer(senderCustomerId, senderWalletId, receiverCustomerId, receiverWalletId, amount);
        return ResponseEntity.ok(transferredWallet);
    }

    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<WalletDTO> withdrawFromWallet(@PathVariable("walletId") Long walletId,
                                                        @RequestParam("customerId") Long customerId,
                                                        @RequestParam("amount") Double amount) {
        WalletDTO updatedWallet = walletService.withdrawFromAccount(walletId, customerId, amount, false);
        return ResponseEntity.ok(updatedWallet);
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<WalletDTO> depositToWallet(@PathVariable Long walletId,
                                                     @RequestParam Double amount) {
        WalletDTO updatedWallet = walletService.depositToWallet(walletId, amount);
        return ResponseEntity.ok(updatedWallet);
    }
}
