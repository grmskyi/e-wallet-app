package com.api.ewalletTask.services.impl;


import com.api.ewalletTask.Exceptions.CustomerBlockedException;
import com.api.ewalletTask.Exceptions.CustomerNotBlockedException;
import com.api.ewalletTask.Exceptions.CustomerNotFoundException;
import com.api.ewalletTask.models.Customer;
import com.api.ewalletTask.repositories.CustomerRepository;
import com.api.ewalletTask.services.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer createOrupdateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public String deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for id: " + id));

        customerRepository.delete(customer);
        return "Customer is deleted";
    }

    @Override
    public Customer blockCustomerForWeek(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for id: " + customerId));

        if (Boolean.TRUE.equals(customer.getBlocked())) {
            LocalDateTime blockedUntil = LocalDateTime.now().plusWeeks(1);
            Date blockedUntilDate = convertToDate(blockedUntil);
            Duration timeLeft = Duration.between(LocalDateTime.now(), blockedUntil);

            customer.setBlockedUntil(blockedUntilDate);
            customer.setTimeLeftToUnblock(timeLeft);
            customerRepository.save(customer);
        }

        return customer;
    }

    @Override
    public void handleBlockedCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for id: " + customerId));

        if (Boolean.TRUE.equals(customer.getBlocked())) {
            LocalDateTime blockedUntil = LocalDateTime.now().plusWeeks(1);
            Duration timeLeft = Duration.between(LocalDateTime.now(), blockedUntil);

            if (timeLeft.toMinutes() > 0) {
                throw new CustomerBlockedException("Customer is blocked and cannot make transactions. Blocked duration remaining: " + timeLeft.toMinutes() + " minutes.");
            } else {
                if (Boolean.TRUE.equals(customer.getUnblockRequested())) {
                    // Unblock the customer
                    customer.setBlocked(false);
                    customer.setBlockedUntil(null);
                    customer.setUnblockRequested(false);
                    customerRepository.save(customer);
                } else {
                    throw new CustomerBlockedException("Customer is blocked. Please request unblocking.");
                }
            }
        }
    }

    @Override
    public void requestUnblocking(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for id: " + customerId));

        if (Boolean.FALSE.equals(customer.getBlocked())) {
            throw new CustomerNotBlockedException("Customer is not blocked.");
        }

        customer.setUnblockRequested(true);
        customerRepository.save(customer);
    }

    private Date convertToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}

