package com.api.ewalletTask.controllers;


import com.api.ewalletTask.Exceptions.CustomerNotBlockedException;
import com.api.ewalletTask.Exceptions.CustomerNotFoundException;
import com.api.ewalletTask.models.Customer;
import com.api.ewalletTask.models.Wallet;
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
    public ResponseEntity<Customer> createNewAccount(@Valid @RequestBody Customer customer){
        return new ResponseEntity<>(customerService.createOrupdateCustomer(customer),HttpStatus.CREATED);
    }


    @DeleteMapping("/customer/{id}")
    public ResponseEntity<String> deleteCustomerAccount(@PathVariable Long id){
        return new ResponseEntity<>(customerService.deleteCustomer(id),HttpStatus.OK);
    }

    @PostMapping("/{customerId}/request-unblocking")
    public ResponseEntity<String> requestUnblocking(@PathVariable Long customerId) {
        try {
            customerService.requestUnblocking(customerId);
            return ResponseEntity.ok("Unblocking request submitted successfully.");
        } catch (CustomerNotFoundException | CustomerNotBlockedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody Wallet wallet){
        return new ResponseEntity<>((walletService.createOrUpdateWallet(wallet)), HttpStatus.CREATED);
    }

    @DeleteMapping("/wallet/{id}")
    public ResponseEntity<String> deleteWallet(@PathVariable Long id){
        return new ResponseEntity<>(walletService.deleteWallet(id),HttpStatus.OK);
    }


    @PostMapping("/transfer-to-another-customer")
    public ResponseEntity<Wallet> transferToAnotherCustomer(@RequestParam Long senderCustomerId,
                                                            @RequestParam Long senderWalletId,
                                                            @RequestParam Long receiverCustomerId,
                                                            @RequestParam Long receiverWalletId,
                                                            @RequestParam Double amount) {

        return new ResponseEntity<>(walletService.transferToAnotherCustomer(senderCustomerId, senderWalletId, receiverCustomerId, receiverWalletId, amount), HttpStatus.OK);
    }

    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<Wallet> withdrawFromWallet(@PathVariable("walletId") Long walletId,
                                                      @RequestParam("customerId") Long customerId,
                                                      @RequestParam("amount") Double amount) {
      return new ResponseEntity<>(walletService.withdrawFromAccount(walletId, customerId, amount,true), HttpStatus.OK);
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<Wallet> depositToWallet(@PathVariable Long walletId,
                                                  @RequestParam Double amount) {
        return new ResponseEntity<>(walletService.depositToWallet(walletId, amount),HttpStatus.OK);
    }
}
