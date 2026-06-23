package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientResponseDTO toResponseDTO(Patient patient);

    @Mapping(target = "dateOfBirth", source = "dateOfBirth", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "registeredDate", source = "registeredDate", dateFormat = "yyyy-MM-dd")
    Patient toEntity(PatientRequestDTO patientRequestDTO);


    @Mapping(target = "dateOfBirth", source = "dateOfBirth", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "registeredDate", ignore = true)
    void updatePatientFromDTO(@MappingTarget Patient patient, PatientRequestDTO patientRequestDTO);
}
