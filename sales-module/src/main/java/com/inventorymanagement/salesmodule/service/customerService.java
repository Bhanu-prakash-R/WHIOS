package com.inventorymanagement.salesmodule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventorymanagement.salesmodule.exception.CustomerNotFoundException;
import com.inventorymanagement.salesmodule.exception.InvalidSaleRequestException;
import com.inventorymanagement.salesmodule.model.ContactDetails;
import com.inventorymanagement.salesmodule.model.Customer;
import com.inventorymanagement.salesmodule.repository.customerRepo;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class customerService {

    @Autowired
    private customerRepo customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(customerService.class);

    /**
     * Get or create a customer based on provided details.
     */
    @Transactional
    public Customer getOrCreateCustomer(String name, String phone, String email, String address) {
        logger.info("Getting or creating customer with phone: {}, email: {}, name: {}", phone, email, name);

        // Check if a customer exists with the exact same phone and email
        Optional<Customer> existingCustomerByPhoneAndEmail = customerRepository.findByContactDetails_PhoneNumberAndContactDetails_Email(phone, email);

        if (existingCustomerByPhoneAndEmail.isPresent()) {
            Customer existing = existingCustomerByPhoneAndEmail.get();
            logger.info("Found existing customer by phone and email: {}", existing.getCustomerId());
            // You might want to update the name and address if they are different
            if (!existing.getName().equals(name) ||
                (existing.getContactDetails() != null && !existing.getContactDetails().getAddress().equals(address))) {
                existing.setName(name);
                if (existing.getContactDetails() == null) {
                    existing.setContactDetails(new ContactDetails());
                }
                existing.getContactDetails().setAddress(address);
                customerRepository.save(existing);
                logger.info("Updated existing customer details: {}", existing.getCustomerId());
            }
            return existing;
        } else {
            // Check if a customer exists with the same phone OR email (but not both)
            Optional<Customer> existingCustomerByPhone = customerRepository.findByContactDetails_PhoneNumber(phone);
            Optional<Customer> existingCustomerByEmail = customerRepository.findByContactDetails_Email(email);

            if (existingCustomerByPhone.isPresent()) {
                throw new InvalidSaleRequestException("Customer with this phone number already exists.");
            }

            if (existingCustomerByEmail.isPresent()) {
                throw new InvalidSaleRequestException("Customer with this email address already exists.");
            }

            // No existing customer found with the same phone or email, create a new one
            logger.info("Creating new customer with phone: {}, email: {}, name: {}", phone, email, name);
            Customer newCustomer = new Customer();
            newCustomer.setName(name);
            ContactDetails contactDetails = new ContactDetails();
            contactDetails.setPhoneNumber(phone);
            contactDetails.setEmail(email);
            contactDetails.setAddress(address);
            newCustomer.setContactDetails(contactDetails);
            return customerRepository.save(newCustomer);
        }
    }

    /**
     * Retrieves all customers from the database.
     */
    public List<Customer> getAllCustomers() {
        try {
            return customerRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching customers", e);
        }
    }

    /**
     * Fetches a Customer by their ID.
     */
    public Customer getCustomerById(UUID id) { // Changed Long to UUID
        try {
            return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
        } catch (Exception e) {
            throw new RuntimeException("Error fetching customer by ID", e);
        }
    }

    /**
     * Updates an existing customer's details, including name and contact information.
     */
    public Customer updateCustomer(UUID id, String name, String phone, String email, String address) { 
        try {
            Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));

            // Update fields
            existingCustomer.setName(name);

            // Update contact details
            ContactDetails contactDetails = existingCustomer.getContactDetails();
            if (contactDetails == null) {
                contactDetails = new ContactDetails(); // Create new ContactDetails if null
            }
            contactDetails.setPhoneNumber(phone);
            contactDetails.setEmail(email);
            contactDetails.setAddress(address);

            existingCustomer.setContactDetails(contactDetails);

            // Save and return updated customer
            return customerRepository.save(existingCustomer);
        } catch (Exception e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    /**
     * Deletes a customer by their ID.
     */
    public void deleteCustomer(UUID id) { 
        try {
            Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));

            customerRepository.delete(existingCustomer);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }
}
