package com.api.ewalletTask.services;

import com.api.ewalletTask.models.Customer;
import com.api.ewalletTask.models.Wallet;

import java.util.List;


public interface CustomerService {

    Customer createOrupdateCustomer(Customer customer);

    String deleteCustomer(Long id);

    Customer blockCustomerForWeek(Long customerId);

    void handleBlockedCustomer(Long customerId);

    void requestUnblocking(Long customerId);

}
