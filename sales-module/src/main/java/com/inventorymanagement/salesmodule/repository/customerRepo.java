package com.inventorymanagement.salesmodule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventorymanagement.salesmodule.model.Customer;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for the Customer entity, extending JpaRepository for CRUD operations.
 * Includes a custom query method to retrieve a Customer by phone number and email from embedded ContactDetails.
 *
 * Custom Method: Custom query to find a customer by phone number and email
 * - findByContactDetails_PhoneNumberAndContactDetails_Email: Fetches a Customer based on their phone number and email.
 */
public interface customerRepo extends JpaRepository<Customer, UUID> { // Changed Long to UUID
    
	Optional<Customer> findByContactDetails_PhoneNumberAndContactDetails_Email(String phoneNumber, String email);
    Optional<Customer> findByContactDetails_PhoneNumber(String phoneNumber);
    Optional<Customer> findByContactDetails_Email(String email);
}
