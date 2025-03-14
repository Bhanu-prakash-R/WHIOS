package inventorymanagement.salesmodule.model;
import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.Email;
@Embeddable
public class ContactDetails {
    @Column(nullable=false)
    private String phoneNumber;
   
    
    @Column(nullable=false)
    @Email(message="email should be valid")
    private String email;
    private String address;

    // Getters and setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}