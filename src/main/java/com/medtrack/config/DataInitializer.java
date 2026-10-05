package com.medtrack.config;

import com.medtrack.model.*;
import com.medtrack.repo.*;
import com.medtrack.service.MedicationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(PatientRepository patients, MedicationService service) {
        return args -> {
            if (patients.count() > 0) return;

            patients.save(new Patient("MRN1001","Ananya Rao","Hyderabad - Ward A"));
            patients.save(new Patient("MRN1002","Rahul Kumar","Hyderabad - Ward B"));
            patients.save(new Patient("MRN1003","Meera Sharma","Secunderabad - Ward C"));
            patients.save(new Patient("MRN1004","Arjun Reddy","Hyderabad - ICU"));

            service.receive(new Lot("LOT-PARA-001","Paracetamol 500mg","MedPharm","Central Pharmacy",1000,
                    LocalDate.now().plusMonths(8),false));
            service.receive(new Lot("LOT-PARA-002","Paracetamol 500mg","MedPharm","Central Pharmacy",700,
                    LocalDate.now().plusMonths(3),false));
            service.receive(new Lot("LOT-INS-001","Insulin 100IU","BioMed","Cold Room",300,
                    LocalDate.now().plusMonths(10),true));
            service.receive(new Lot("LOT-INS-002","Insulin 100IU","BioMed","Cold Room",200,
                    LocalDate.now().plusMonths(5),true));

            service.repackage("LOT-PARA-001",100,"LOT-PARA-001-CHILD-001");
            service.dispense("Paracetamol 500mg","MRN1001",30);
            service.dispense("Paracetamol 500mg","MRN1002",20);
            service.dispense("Paracetamol 500mg","MRN1003",15);
        };
    }
}
