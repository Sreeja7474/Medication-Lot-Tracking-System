package com.medtrack.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="lot_genealogy", indexes = {
    @Index(name="idx_genealogy_parent", columnList="parentLot"),
    @Index(name="idx_genealogy_child", columnList="childLot")
})
public class LotRelation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false) private String parentLot;
    @Column(nullable=false) private String childLot;
    @Column(nullable=false) private String relationType;
    private int quantity;
    private LocalDateTime createdAt;

    public LotRelation(){}
    public LotRelation(String p,String c,String type,int qty){parentLot=p;childLot=c;relationType=type;quantity=qty;createdAt=LocalDateTime.now();}
    public Long getId(){return id;} public String getParentLot(){return parentLot;} public String getChildLot(){return childLot;}
    public String getRelationType(){return relationType;} public int getQuantity(){return quantity;} public LocalDateTime getCreatedAt(){return createdAt;}
}
