package com.inventorymanagement.salesmodule.model;
//import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.Email;
/**
 * Marks a class as embeddable to be used as a component of another entity.
 * The @Embeddable annotation indicates that the class doesn't have a primary key of its own
 * and its attributes are mapped to columns of the owning entity's table.
 */
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