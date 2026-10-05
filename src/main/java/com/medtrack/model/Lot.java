package com.medtrack.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="medication_lots", indexes = {
    @Index(name="idx_lot_number", columnList="lotNumber"),
    @Index(name="idx_lot_expiry", columnList="expiryDate"),
    @Index(name="idx_lot_status", columnList="status")
})
public class Lot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String lotNumber;

    @Column(nullable=false)
    private String medicationName;

    @Column(nullable=false)
    private String manufacturer;

    @Column(nullable=false)
    private String location;

    @Column(nullable=false)
    private int quantity;

    @Column(nullable=false)
    private int availableQuantity;

    @Column(nullable=false)
    private LocalDate expiryDate;

    private boolean coldChainRequired;
    private boolean coldChainExcursion;
    private String status;
    private String parentLotNumber;
    private LocalDateTime receivedAt;

    public Lot() {}

    public Lot(String lotNumber, String medicationName, String manufacturer, String location,
               int quantity, LocalDate expiryDate, boolean coldChainRequired) {
        this.lotNumber=lotNumber; this.medicationName=medicationName; this.manufacturer=manufacturer;
        this.location=location; this.quantity=quantity; this.availableQuantity=quantity;
        this.expiryDate=expiryDate; this.coldChainRequired=coldChainRequired;
        this.status="AVAILABLE"; this.receivedAt=LocalDateTime.now();
    }

    public Long getId(){return id;} public String getLotNumber(){return lotNumber;}
    public String getMedicationName(){return medicationName;} public String getManufacturer(){return manufacturer;}
    public String getLocation(){return location;} public int getQuantity(){return quantity;}
    public int getAvailableQuantity(){return availableQuantity;} public LocalDate getExpiryDate(){return expiryDate;}
    public boolean isColdChainRequired(){return coldChainRequired;} public boolean isColdChainExcursion(){return coldChainExcursion;}
    public String getStatus(){return status;} public String getParentLotNumber(){return parentLotNumber;}
    public LocalDateTime getReceivedAt(){return receivedAt;}
    public void setLocation(String v){location=v;} public void setAvailableQuantity(int v){availableQuantity=v;}
    public void setColdChainExcursion(boolean v){coldChainExcursion=v;}
    public void setStatus(String v){status=v;} public void setParentLotNumber(String v){parentLotNumber=v;}
}
