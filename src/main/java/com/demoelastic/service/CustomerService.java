package com.demoelastic.service;

import com.demoelastic.domain.Customer;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Customer}.
 */
public interface CustomerService {
    /**
     * Save a customer.
     *
     * @param customer the entity to save.
     * @return the persisted entity.
     */
    Customer save(Customer customer);

    /**
     * Updates a customer.
     *
     * @param customer the entity to update.
     * @return the persisted entity.
     */
    Customer update(Customer customer);

    /**
     * Partially updates a customer.
     *
     * @param customer the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Customer> partialUpdate(Customer customer);

    /**
     * Get all the customers.
     *
     * @return the list of entities.
     */
    List<Customer> findAll();

    /**
     * Get the "id" customer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Customer> findOne(String id);

    /**
     * Delete the "id" customer.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    /**
     * Search for the customer corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<Customer> search(String query);
}
