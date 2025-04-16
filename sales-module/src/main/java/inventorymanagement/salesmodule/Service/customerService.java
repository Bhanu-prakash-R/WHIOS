package inventorymanagement.salesmodule.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import inventorymanagement.salesmodule.model.Customer;
import inventorymanagement.salesmodule.model.ContactDetails;
import inventorymanagement.salesmodule.Repository.customerRepo;
import inventorymanagement.salesmodule.exception.CustomerNotFoundException;
import inventorymanagement.salesmodule.exception.InvalidSaleRequestException;
import java.util.List;
import java.util.UUID;

@Service
public class customerService {

    @Autowired
    private customerRepo customerRepository;

    /**
     * Get or create a customer based on provided details.
     */
    public Customer getOrCreateCustomer(String name, String phone, String email, String address) {
        // Check if a customer exists by phone number and email
        Customer existingCustomer = customerRepository.findByContactDetails_PhoneNumberAndContactDetails_Email(phone, email);
        if (existingCustomer != null) {
            // Update existing customer details
            existingCustomer.setName(name);
            ContactDetails contactDetails = existingCustomer.getContactDetails();
            if (contactDetails == null) {
                contactDetails = new ContactDetails();
                
            }
            contactDetails.setPhoneNumber(phone);
            contactDetails.setEmail(email);
            
            contactDetails.setAddress(address);
            existingCustomer.setContactDetails(contactDetails);
            return customerRepository.save(existingCustomer);
        }

        // Create a new customer if not found
        Customer newCustomer = new Customer();
        newCustomer.setName(name);

        // Populate contact details
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setPhoneNumber(phone);
        contactDetails.setEmail(email);
        contactDetails.setAddress(address);
        newCustomer.setContactDetails(contactDetails);

        // Save and return the new customer
        try {
            return customerRepository.save(newCustomer);
        } catch (Exception e) {
            throw new InvalidSaleRequestException("Error creating customer");
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
