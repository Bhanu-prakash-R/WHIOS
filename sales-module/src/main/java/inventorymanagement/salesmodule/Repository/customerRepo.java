package inventorymanagement.salesmodule.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import inventorymanagement.salesmodule.model.Customer;

public interface customerRepo extends JpaRepository<Customer, Long> {
    // Custom query to find a customer by phone number and email
    Customer findByContactDetails_PhoneNumberAndContactDetails_Email(String phoneNumber, String email);
}
