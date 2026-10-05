package com.medtrack.model;

import jakarta.persistence.*;

@Entity
@Table(name="patients", indexes=@Index(name="idx_patient_mrn", columnList="mrn"))
public class Patient {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true) private String mrn;
    @Column(nullable=false) private String name;
    private String location;

    public Patient() {}
    public Patient(String mrn,String name,String location){this.mrn=mrn;this.name=name;this.location=location;}
    public Long getId(){return id;} public String getMrn(){return mrn;} public String getName(){return name;} public String getLocation(){return location;}
}
