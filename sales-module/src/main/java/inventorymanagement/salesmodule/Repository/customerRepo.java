package inventorymanagement.salesmodule.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import inventorymanagement.salesmodule.model.Customer;

/**
 * Repository interface for the Customer entity, extending JpaRepository for CRUD operations.
 * Includes a custom query method to retrieve a Customer by phone number and email from embedded ContactDetails.
 *
 * Custom Method:Custom query to find a customer by phone number and email
 * - findByContactDetails_PhoneNumberAndContactDetails_Email: Fetches a Customer based on their phone number and email.
 */
public interface customerRepo extends JpaRepository<Customer, Long> {
    
    Customer findByContactDetails_PhoneNumberAndContactDetails_Email(String phoneNumber, String email);
}
