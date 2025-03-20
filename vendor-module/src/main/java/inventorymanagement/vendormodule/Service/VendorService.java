package inventorymanagement.vendormodule.Service;

import inventorymanagement.vendormodule.Dto.VendorRequestDto;
import inventorymanagement.vendormodule.Dto.VendorResponseDto;
import inventorymanagement.vendormodule.Dto.ContactDetailsDto;
import inventorymanagement.vendormodule.Entity.Vendor;
import inventorymanagement.vendormodule.Entity.ContactDetails;
import inventorymanagement.vendormodule.Repository.VendorDao;
import inventorymanagement.vendormodule.exception.InvalidVendorRequestException;
import inventorymanagement.vendormodule.exception.VendorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        try {
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
        } catch (Exception e) {
            logger.error("Error fetching vendor by ID: {}", id, e);
            throw new RuntimeException("Unable to fetch vendor by ID", e);
        }
    }

    /**
     * Saves a new vendor to the database.
     *
     * @param vendorRequestDto The vendor details to be saved.
     * @return A VendorResponseDto representing the saved vendor.
     * @throws InvalidVendorRequestException if validation fails for the vendor data.
     */
    public VendorResponseDto saveVendor(VendorRequestDto vendorRequestDto) {
        logger.info("Saving vendor: {}", vendorRequestDto.getVendorName());
        try {
            validateVendor(vendorRequestDto);
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
        } catch (Exception e) {
            logger.error("Error saving vendor: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to save vendor", e);
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
        try {
            Vendor vendor = vendorRepository.findById(id)
                    .orElseThrow(() -> new VendorNotFoundException("Vendor not found with id: " + id));
            vendorRepository.delete(vendor);
        } catch (Exception e) {
            logger.error("Error deleting vendor with ID: {}", id, e);
            throw new RuntimeException("Unable to delete vendor", e);
        }
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
        try {
            List<Vendor> vendors = vendorRepository.findByVendorNameContaining(vendorName);
            if (vendors.isEmpty()) {
                throw new VendorNotFoundException("No vendors found with name containing: " + vendorName);
            }
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
            logger.error("Error searching vendors by name: {}", vendorName, e);
            throw new RuntimeException("Unable to search vendors", e);
        }
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
