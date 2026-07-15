package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.DuplicatedEmailException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;
    private final PatientMapper patientMapper;


    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
        this.patientMapper = patientMapper;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        //return patientMapper.toResponseDTO(patients); // Convert a patient to a PatientResponseDTO
        return patients
                .stream()
                .map(patientMapper::toResponseDTO).toList();
    }


    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.email())){
            throw new DuplicatedEmailException(
                    "El email " + patientRequestDTO.email() + " ya está registrado");
        }

        Patient patient = patientMapper.toEntity(patientRequestDTO);
        Patient savedPatient = patientRepository.save(patient);

        // Llamada al servicio de facturación para crear una cuenta de facturación
        billingServiceGrpcClient.createBillingAccount(
                savedPatient.getId().toString(),
                savedPatient.getName(),
                savedPatient.getEmail()
        );

        kafkaProducer.sendEvent(savedPatient);

        return  patientMapper.toResponseDTO(savedPatient);
    }


    public PatientResponseDTO updatePatient (
            UUID id, PatientRequestDTO patientRequestDTO){

        Patient patient = patientRepository
                .findById(id)
                .orElseThrow(
                        () -> new PatientNotFoundException("Paciente con ID: " + id + " no fue encontrado"));

        //¿Otro id, pero con el mismo email?
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.email(), id))
            throw new DuplicatedEmailException(
                    "El email " + patientRequestDTO.email() + " ya está registrado");

        patientMapper.updatePatientFromDTO(patient, patientRequestDTO);
        Patient updatedPatient = patientRepository.save(patient);

        return patientMapper.toResponseDTO(updatedPatient);
    }


    public void deletePatient(UUID id){
        patientRepository.deleteById(id);
    }
}
