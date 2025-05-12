package com.inventorymanagement.vendormodule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventorymanagement.vendormodule.dto.ContactDetailsDto;
import com.inventorymanagement.vendormodule.dto.VendorRequestDto;
import com.inventorymanagement.vendormodule.dto.VendorResponseDto;
import com.inventorymanagement.vendormodule.entity.ContactDetails;
import com.inventorymanagement.vendormodule.entity.Vendor;
import com.inventorymanagement.vendormodule.exception.InvalidVendorRequestException;
import com.inventorymanagement.vendormodule.exception.VendorNotFoundException;
import com.inventorymanagement.vendormodule.repository.VendorDao;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing vendor-related operations.
 * Provides business logic for creating, updating, deleting, fetching, and searching vendors.
 * Uses VendorDao for database interactions.
 */
@Service
public class VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);

    @Autowired
    private VendorDao vendorRepository;

    /**
     * Fetches all vendors from the database.
     *
     * @return A list of VendorResponseDto representing all vendors.
     */
    public List<VendorResponseDto> getAllVendors() {
        logger.info("Fetching all vendors");
        try {
            List<Vendor> vendors = vendorRepository.findAll();
            return vendors.stream()
                    .map(vendor -> new VendorResponseDto(
                            vendor.getVendorId(),
                            vendor.getVendorName(),
                            new ContactDetailsDto(
                                    vendor.getContactDetails().getPhoneNumber(),
                                    vendor.getContactDetails().getEmail(),
                                    vendor.getContactDetails().getAddress()
                            )
                    )).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all vendors: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to fetch vendors", e);
        }
    }

    /**
     * Fetches a specific vendor by ID.
     *
     * @param id The UUID of the vendor.
     * @return A VendorResponseDto representing the vendor.
     * @throws VendorNotFoundException if the vendor with the given ID is not found.
     */
    public VendorResponseDto getVendorById(UUID id) {
        logger.info("Fetching vendor with ID: {}", id);

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorNotFoundException("Vendor not found with id: " + id));

        return new VendorResponseDto(
                vendor.getVendorId(),
                vendor.getVendorName(),
                new ContactDetailsDto(
                        vendor.getContactDetails().getPhoneNumber(),
                        vendor.getContactDetails().getEmail(),
                        vendor.getContactDetails().getAddress()
                )
        );
    }


    /**
     * Saves a new vendor to the database.
     *
     * @param vendorRequestDto The vendor details to be saved.
     * @return A VendorResponseDto representing the saved vendor.
     * @throws InvalidVendorRequestException if validation fails for the vendor data.
     */
    @Transactional
    public VendorResponseDto saveVendor(VendorRequestDto vendorRequestDto) {
        logger.info("Saving vendor: {}", vendorRequestDto.getVendorName());
        try {
            validateVendorUniqueness(vendorRequestDto.getPhoneNumber(), vendorRequestDto.getEmail()); // Added Validation
            Vendor vendor = new Vendor();
            vendor.setVendorName(vendorRequestDto.getVendorName());
            ContactDetails contactDetails = new ContactDetails();
            contactDetails.setPhoneNumber(vendorRequestDto.getPhoneNumber());
            contactDetails.setEmail(vendorRequestDto.getEmail());
            contactDetails.setAddress(vendorRequestDto.getAddress());
            vendor.setContactDetails(contactDetails);

            Vendor savedVendor = vendorRepository.save(vendor);

            return new VendorResponseDto(
                    savedVendor.getVendorId(),
                    savedVendor.getVendorName(),
                    new ContactDetailsDto(
                            savedVendor.getContactDetails().getPhoneNumber(),
                            savedVendor.getContactDetails().getEmail(),
                            savedVendor.getContactDetails().getAddress()
                    )
            );
        } catch (RuntimeException e) {
            logger.error("Error saving vendor: {}", e.getMessage(), e);
            throw e; // Re-throw the exception after logging
        }
    }

    private void validateVendorUniqueness(String phoneNumber, String email) {
        Optional<Vendor> existingVendorWithPhone = vendorRepository.findByContactDetailsPhoneNumber(phoneNumber);
        if (existingVendorWithPhone.isPresent()) {
            throw new IllegalArgumentException("Vendor with this phone number already exists");
        }

        Optional<Vendor> existingVendorWithEmail = vendorRepository.findByContactDetailsEmail(email);
        if (existingVendorWithEmail.isPresent()) {
            throw new IllegalArgumentException("Vendor with this email already exists");
        }
    }

    /**
     * Updates an existing vendor in the database.
     *
     * @param id The UUID of the vendor to update.
     * @param vendorRequestDto The updated vendor details.
     * @return A VendorResponseDto representing the updated vendor.
     * @throws VendorNotFoundException if the vendor with the given ID is not found.
     * @throws InvalidVendorRequestException if validation fails for the vendor data.
     */
    public VendorResponseDto updateVendor(UUID id, VendorRequestDto vendorRequestDto) {
        logger.info("Updating vendor with ID: {}", id);
        try {
            Vendor vendor = vendorRepository.findById(id)
                    .orElseThrow(() -> new VendorNotFoundException("Vendor not found with id: " + id));

            validateVendor(vendorRequestDto);

            vendor.setVendorName(vendorRequestDto.getVendorName());
            ContactDetails contactDetails = vendor.getContactDetails();
            contactDetails.setPhoneNumber(vendorRequestDto.getPhoneNumber());
            contactDetails.setEmail(vendorRequestDto.getEmail());
            contactDetails.setAddress(vendorRequestDto.getAddress());
            vendor.setContactDetails(contactDetails);

            Vendor updatedVendor = vendorRepository.save(vendor);

            return new VendorResponseDto(
                    updatedVendor.getVendorId(),
                    updatedVendor.getVendorName(),
                    new ContactDetailsDto(
                            updatedVendor.getContactDetails().getPhoneNumber(),
                            updatedVendor.getContactDetails().getEmail(),
                            updatedVendor.getContactDetails().getAddress()
                    )
            );
        } catch (Exception e) {
            logger.error("Error updating vendor: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to update vendor", e);
        }
    }

    /**
     * Deletes a vendor by ID.
     *
     * @param id The UUID of the vendor to delete.
     * @throws VendorNotFoundException if the vendor with the given ID is not found.
     */
    public void deleteVendor(UUID id) {
        logger.info("Deleting vendor with ID: {}", id);
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorNotFoundException("Vendor not found with id: " + id));
        vendorRepository.delete(vendor);
        logger.info("Vendor with ID: {} has been successfully deleted.", id);
    }

    /**
     * Searches for vendors by name.
     *
     * @param vendorName The name keyword to search for.
     * @return A list of VendorResponseDto representing matching vendors.
     * @throws VendorNotFoundException if no matching vendors are found.
     */
    public List<VendorResponseDto> searchVendorsByName(String vendorName) {
        logger.info("Searching vendors by name: {}", vendorName);

        List<Vendor> vendors = vendorRepository.findByVendorNameContaining(vendorName);

        // Throw exception if no vendors are found
        if (vendors.isEmpty()) {
            logger.warn("No vendors found with name containing: {}", vendorName);
            throw new VendorNotFoundException("No vendors found with name containing: " + vendorName);
        }

        // Convert the list of Vendor entities to VendorResponseDto
        return vendors.stream()
                .map(vendor -> new VendorResponseDto(
                        vendor.getVendorId(),
                        vendor.getVendorName(),
                        new ContactDetailsDto(
                                vendor.getContactDetails().getPhoneNumber(),
                                vendor.getContactDetails().getEmail(),
                                vendor.getContactDetails().getAddress()
                        )
                )).collect(Collectors.toList());
    }

    /**
     * Fetches the names of all vendors.
     *
     * @return A list of strings containing vendor names.
     */
    public List<String> getAllVendorNames() {
        logger.info("Fetching all vendor names");
        return vendorRepository.findAll().stream()
                .map(Vendor::getVendorName)
                .collect(Collectors.toList());
    }

    /**
     * Validates the VendorRequestDto to ensure it meets the requirements.
     *
     * @param vendorRequestDto The vendor data to validate.
     * @throws InvalidVendorRequestException if validation fails.
     */
    private void validateVendor(VendorRequestDto vendorRequestDto) {
        if (vendorRequestDto.getVendorName() == null || vendorRequestDto.getVendorName().isEmpty()) {
            throw new InvalidVendorRequestException("Vendor name is required");
        }
        if (vendorRequestDto.getPhoneNumber() == null || vendorRequestDto.getPhoneNumber().isEmpty()) {
            throw new InvalidVendorRequestException("Phone number is required");
        }
        if (vendorRequestDto.getEmail() == null || vendorRequestDto.getEmail().isEmpty()) {
            throw new InvalidVendorRequestException("Email is required");
        }
        if (vendorRequestDto.getAddress() == null || vendorRequestDto.getAddress().isEmpty()) {
            throw new InvalidVendorRequestException("Address is required");
        }
    }
    
}
