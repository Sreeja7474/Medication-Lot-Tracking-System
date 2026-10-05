package com.medtrack.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="recalls")
public class Recall {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false,unique=true) private String recallNumber;
    @Column(nullable=false) private String rootLot;
    private String reason;
    private String status;
    private LocalDateTime createdAt;

    public Recall(){}
    public Recall(String n,String root,String reason){recallNumber=n;rootLot=root;this.reason=reason;status="OPEN";createdAt=LocalDateTime.now();}
    public Long getId(){return id;} public String getRecallNumber(){return recallNumber;} public String getRootLot(){return rootLot;}
    public String getReason(){return reason;} public String getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;}
    public void setStatus(String s){status=s;}
}
