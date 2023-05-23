package com.api.ewalletTask.services.impl;


import com.api.ewalletTask.Exceptions.BaseException;
import com.api.ewalletTask.dtos.CustomerDTO;
import com.api.ewalletTask.models.Customer;
import com.api.ewalletTask.repositories.CustomerRepository;
import com.api.ewalletTask.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @Override
    public CustomerDTO createOrUpdateCustomer(CustomerDTO customerDTO) {
        Customer customer = objectMapper.convertValue(customerDTO, Customer.class);
        Customer savedCustomer = customerRepository.save(customer);
        return objectMapper.convertValue(savedCustomer, CustomerDTO.class);
    }

    @Override
    public String deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
        return "Customer is deleted";
    }

    @Override
    public void blockCustomerForWeek(Long customerId) {
        Customer customer = getCustomerById(customerId);
        if (Boolean.TRUE.equals(customer.getBlocked())) {
            LocalDateTime blockedUntil = LocalDateTime.now().plusWeeks(1);
            Date blockedUntilDate = convertToDate(blockedUntil);
            Duration timeLeft = Duration.between(LocalDateTime.now(), blockedUntil);

            customer.setBlockedUntil(blockedUntilDate);
            customer.setTimeLeftToUnblock(timeLeft);
            customerRepository.save(customer);
        }
        convertToCustomerDTO(customer);
    }

    @Override
    public void handleBlockedCustomer(Long customerId) {
        Customer customer = getCustomerById(customerId);
        if (Boolean.TRUE.equals(customer.getBlocked())) {
            LocalDateTime blockedUntil = LocalDateTime.now().plusWeeks(1);
            Duration timeLeft = Duration.between(LocalDateTime.now(), blockedUntil);

            if (timeLeft.toMinutes() > 0) {
                throw new BaseException("Customer is blocked and cannot make transactions. Blocked duration remaining: " + timeLeft.toMinutes() + " minutes.");
            } else {
                if (Boolean.TRUE.equals(customer.getUnblockRequested())) {
                    // Unblock the customer
                    customer.setBlocked(false);
                    customer.setBlockedUntil(null);
                    customer.setUnblockRequested(false);
                    customerRepository.save(customer);
                } else {
                    throw new BaseException("Customer is blocked. Please request unblocking.");
                }
            }
        }
    }

    @Override
    public void requestUnblocking(Long customerId) {
        Customer customer = getCustomerById(customerId);
        if (Boolean.FALSE.equals(customer.getBlocked())) {
            throw new BaseException("Customer is not blocked.");
        }
        customer.setUnblockRequested(true);
        customerRepository.save(customer);
    }

    private Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new BaseException("Customer not found for id: " + id));
    }

    private Date convertToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private void convertToCustomerDTO(Customer customer) {
        objectMapper.convertValue(customer, CustomerDTO.class);
    }
}


