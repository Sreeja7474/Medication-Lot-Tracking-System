package com.medtrack.controller;

import com.medtrack.model.*;
import com.medtrack.repo.PatientRepository;
import com.medtrack.service.MedicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class MedicationController {
    private final MedicationService service;
    private final PatientRepository patients;
    

    public MedicationController(MedicationService service,PatientRepository patients){this.service=service;this.patients=patients;}

    @GetMapping("/lots") public List<Lot> lots(){return service.allLots();}
    @GetMapping("/patients") public List<Patient> patients(){return service.allPatients();}

    @PostMapping("/lots/receive") public ResponseEntity<?> receive(@RequestBody Map<String,Object> b){
        try{
            Lot l=new Lot(
                (String)b.get("lotNumber"),(String)b.get("medicationName"),(String)b.get("manufacturer"),
                (String)b.getOrDefault("location","Main Pharmacy"),((Number)b.getOrDefault("quantity",0)).intValue(),
                LocalDate.parse((String)b.get("expiryDate")),Boolean.TRUE.equals(b.get("coldChainRequired")));
            return ResponseEntity.ok(service.receive(l));
        }catch(Exception e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
    }

    @PostMapping("/lots/{lot}/cold-chain") public ResponseEntity<?> cold(@PathVariable String lot){
        try{return ResponseEntity.ok(service.coldChainFlag(lot));}
        catch(Exception e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
    }

    @GetMapping("/fefo") public ResponseEntity<?> fefo(@RequestParam String medication,@RequestParam int quantity){
        return ResponseEntity.ok(service.fefo(medication,quantity));
    }

    @PostMapping("/dispense") public ResponseEntity<?> dispense(@RequestBody Map<String,Object> b){
        try{return ResponseEntity.ok(service.dispense((String)b.get("medication"),(String)b.get("mrn"),((Number)b.get("quantity")).intValue()));}
        catch(Exception e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
    }

    @PostMapping("/lots/repackage") public ResponseEntity<?> repackage(@RequestBody Map<String,Object> b){
        try{return ResponseEntity.ok(service.repackage((String)b.get("parentLot"),((Number)b.get("quantity")).intValue(),(String)b.get("childLotNumber")));}
        catch(Exception e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
    }

    @PostMapping("/recall/impact") public ResponseEntity<?> recall(@RequestParam String lot){
        try{return ResponseEntity.ok(service.recallImpact(lot));}
        catch(Exception e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
    }
}
