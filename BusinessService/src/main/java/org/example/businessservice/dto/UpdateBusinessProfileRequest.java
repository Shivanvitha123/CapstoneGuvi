package org.example.businessservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.businessservice.model.BusinessType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateBusinessProfileRequest(

        @NotBlank(message = "Business name is required")
        @Size(max = 200, message = "Business name cannot exceed 200 characters")
        String businessName,

        @NotNull(message = "Business type is required")
        BusinessType businessType,

        @NotBlank(message = "Industry is required")
        String industry,

        @NotBlank(message = "Address is required")
        @Size(max = 500, message = "Address cannot exceed 500 characters")
        String address,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Postal code is required")
        String postalCode,

        @NotBlank(message = "Country is required")
        String country,

        @NotBlank(message = "Contact email is required")
        @Email(message = "Invalid contact email")
        String contactEmail,

        @NotBlank(message = "Contact phone is required")
        @Pattern(
                regexp = "^[0-9+()\\- ]{7,30}$",
                message = "Invalid contact phone"
        )
        String contactPhone,

        @Min(value = 0, message = "Annual revenue cannot be negative")
        BigDecimal annualRevenue,

        @Min(value = 0, message = "Employee count cannot be negative")
        Integer employeeCount,

        @PastOrPresent(message = "Established date cannot be in the future")
        LocalDate establishedDate
) {
}

