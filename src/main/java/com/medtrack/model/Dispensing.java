package com.medtrack.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="dispensing", indexes = {
    @Index(name="idx_disp_lot", columnList="lotNumber"),
    @Index(name="idx_disp_patient", columnList="patientMrn")
})
public class Dispensing {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false) private String lotNumber;
    @Column(nullable=false) private String patientMrn;
    @Column(nullable=false) private String patientName;
    @Column(nullable=false) private String location;
    @Column(nullable=false) private int quantity;
    @Column(nullable=false) private LocalDateTime dispensedAt;

    public Dispensing(){}
    public Dispensing(String lot,String mrn,String name,String loc,int qty){
        lotNumber=lot;patientMrn=mrn;patientName=name;location=loc;quantity=qty;dispensedAt=LocalDateTime.now();
    }
    public Long getId(){return id;} public String getLotNumber(){return lotNumber;} public String getPatientMrn(){return patientMrn;}
    public String getPatientName(){return patientName;} public String getLocation(){return location;} public int getQuantity(){return quantity;}
    public LocalDateTime getDispensedAt(){return dispensedAt;}
}
