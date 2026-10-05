package com.medtrack.repo;
import com.medtrack.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PatientRepository extends JpaRepository<Patient,Long>{ Optional<Patient> findByMrn(String mrn); }
