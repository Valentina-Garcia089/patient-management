package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PatientRequestDTO(
        @NotBlank(message = "Name is mandatory")
        @Size(max = 80, message = "Name can't exceed 80 characters")
        String name,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Address is mandatory")
        String address,

        @NotBlank(message = "Date of Birth is mandatory")
        String dateOfBirth,

        @NotBlank(groups = CreatePatientValidationGroup.class, message = "Registered Date is mandatory")
        String registeredDate
) {
}
