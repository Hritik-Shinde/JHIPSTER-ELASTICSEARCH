package com.demoelastic.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.demoelastic.domain.Customer;
import com.demoelastic.repository.CustomerRepository;
import com.demoelastic.repository.search.CustomerSearchRepository;
import com.demoelastic.service.CustomerService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Customer}.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    private final CustomerSearchRepository customerSearchRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerSearchRepository customerSearchRepository) {
        this.customerRepository = customerRepository;
        this.customerSearchRepository = customerSearchRepository;
    }

    @Override
    public Customer save(Customer customer) {
        log.info("Request to save Customer : {}", customer);
        customer.setId(UUID.randomUUID().toString());
        
        Customer result = customerRepository.save(customer);
        customerSearchRepository.index(result);
        return result;
    }

    @Override
    public Customer update(Customer customer) {
        log.info("Request to update Customer : {}", customer);
        customer.setIsPersisted();
        Customer result = customerRepository.save(customer);
        customerSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Customer> partialUpdate(Customer customer) {
        log.debug("Request to partially update Customer : {}", customer);

        return customerRepository
            .findById(customer.getId())
            .map(existingCustomer -> {
                if (customer.getFirstName() != null) {
                    existingCustomer.setFirstName(customer.getFirstName());
                }
                if (customer.getLastName() != null) {
                    existingCustomer.setLastName(customer.getLastName());
                }
                if (customer.getAge() != null) {
                    existingCustomer.setAge(customer.getAge());
                }

                return existingCustomer;
            })
            .map(customerRepository::save)
            .map(savedCustomer -> {
                customerSearchRepository.save(savedCustomer);

                return savedCustomer;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        log.debug("Request to get all Customers");
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findOne(String id) {
        log.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Customer : {}", id);
        customerRepository.deleteById(id);
        customerSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> search(String query) {
        log.debug("Request to search Customers for query {}", query);
        return StreamSupport.stream(customerSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
