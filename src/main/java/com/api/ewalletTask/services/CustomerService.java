package com.api.ewalletTask.services;

import com.api.ewalletTask.dtos.CustomerDTO;


public interface CustomerService {

    CustomerDTO createOrUpdateCustomer(CustomerDTO customerDTO);
    String deleteCustomer(Long id);
    void blockCustomerForWeek(Long customerId);
    void handleBlockedCustomer(Long customerId);
    void requestUnblocking(Long customerId);

}
