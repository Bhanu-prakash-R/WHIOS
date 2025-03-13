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

@Service
public class VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);

    @Autowired
    private VendorDao vendorRepository;

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
        } catch (VendorNotFoundException e) {
            logger.error("Vendor not found with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching vendor by ID: {}", id, e);
            throw new RuntimeException("Unable to fetch vendor by ID", e);
        }
    }

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
        } catch (InvalidVendorRequestException e) {
            logger.error("Invalid vendor request: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error saving vendor: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to save vendor", e);
        }
    }

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
        } catch (VendorNotFoundException e) {
            logger.error("Vendor not found with ID: {}", id, e);
            throw e;
        } catch (InvalidVendorRequestException e) {
            logger.error("Invalid vendor request: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating vendor: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to update vendor", e);
        }
    }

    public void deleteVendor(UUID id) {
        logger.info("Deleting vendor with ID: {}", id);
        try {
            Vendor vendor = vendorRepository.findById(id)
                    .orElseThrow(() -> new VendorNotFoundException("Vendor not found with id: " + id));
            vendorRepository.delete(vendor);
        } catch (VendorNotFoundException e) {
            logger.error("Vendor not found with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting vendor with ID: {}", id, e);
            throw new RuntimeException("Unable to delete vendor", e);
        }
    }

    public List<VendorResponseDto> searchVendorsByName(String vendorName) {
        logger.info("Searching vendors by name: {}", vendorName);
        try {
            List<Vendor> vendors = vendorRepository.findByVendorNameContaining(vendorName); // Updated method call
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
        } catch (VendorNotFoundException e) {
            logger.error("No vendors found with name containing: {}", vendorName, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error searching vendors by name: {}", vendorName, e);
            throw new RuntimeException("Unable to search vendors", e);
        }
    }

    public List<String> getAllVendorNames() {
        logger.info("Fetching all vendor names");
        return vendorRepository.findAll().stream()
                .map(Vendor::getVendorName)  // Use method reference to get vendorName
                .collect(Collectors.toList());
    }

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
