package com.medtrack.service;

import com.medtrack.model.*;
import com.medtrack.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class MedicationService {
    private final LotRepository lots;
    private final PatientRepository patients;
    private final DispensingRepository dispensing;
    private final LotRelationRepository relations;
    private final RecallRepository recalls;

    public MedicationService(LotRepository lots,PatientRepository patients,DispensingRepository dispensing,
                             LotRelationRepository relations,RecallRepository recalls){
        this.lots=lots;this.patients=patients;this.dispensing=dispensing;this.relations=relations;this.recalls=recalls;
    }

    @Transactional
    public Lot receive(Lot lot){
        if(lots.findByLotNumber(lot.getLotNumber()).isPresent()) throw new IllegalArgumentException("Lot already exists");
        lot.setStatus("AVAILABLE");
        return lots.save(lot);
    }

    @Transactional
    public Map<String,Object> coldChainFlag(String lotNumber){
        Lot lot=getLot(lotNumber);
        lot.setColdChainExcursion(true);
        lot.setStatus("QUARANTINED");
        lots.save(lot);
        return Map.of("lot",lotNumber,"flagged",true,"status","QUARANTINED",
                "message","Cold-chain excursion flagged; lot removed from FEFO picking.");
    }

    public List<Lot> fefo(String medication,int quantity){
        return lots.findByMedicationNameAndAvailableQuantityGreaterThanAndStatusOrderByExpiryDateAsc(
                medication, quantity-1, "AVAILABLE");
    }

    @Transactional
    public Map<String,Object> dispense(String medication,String mrn,int quantity){
        Patient p=patients.findByMrn(mrn).orElseThrow(()->new IllegalArgumentException("Patient not found"));
        int remaining=quantity; List<Map<String,Object>> picks=new ArrayList<>();
        List<Lot> candidates=lots.findByMedicationNameAndAvailableQuantityGreaterThanAndStatusOrderByExpiryDateAsc(
                medication,0,"AVAILABLE");
        candidates.removeIf(l -> l.getExpiryDate().isBefore(LocalDate.now()) || l.isColdChainExcursion());

        for(Lot lot:candidates){
            if(remaining<=0) break;
            int take=Math.min(remaining,lot.getAvailableQuantity());
            lot.setAvailableQuantity(lot.getAvailableQuantity()-take);
            if(lot.getAvailableQuantity()==0) lot.setStatus("DEPLETED");
            lots.save(lot);
            dispensing.save(new Dispensing(lot.getLotNumber(),p.getMrn(),p.getName(),p.getLocation(),take));
            picks.add(Map.of("lotNumber",lot.getLotNumber(),"expiryDate",lot.getExpiryDate(),"quantity",take));
            remaining-=take;
        }
        if(remaining>0) throw new IllegalStateException("Insufficient FEFO-eligible stock. Short by "+remaining);
        return Map.of("patient",p.getMrn(),"medication",medication,"requested",quantity,"picks",picks,"strategy","FEFO");
    }

    @Transactional
    public Lot repackage(String parentLot,int childQuantity,String childLotNumber){
        Lot parent=getLot(parentLot);
        if(parent.getAvailableQuantity()<childQuantity) throw new IllegalArgumentException("Insufficient parent lot quantity");
        if(lots.findByLotNumber(childLotNumber).isPresent()) throw new IllegalArgumentException("Child lot already exists");
        parent.setAvailableQuantity(parent.getAvailableQuantity()-childQuantity);
        if(parent.getAvailableQuantity()==0) parent.setStatus("DEPLETED");
        lots.save(parent);
        Lot child=new Lot(childLotNumber,parent.getMedicationName(),parent.getManufacturer(),
                parent.getLocation(),childQuantity,parent.getExpiryDate(),parent.isColdChainRequired());
        child.setParentLotNumber(parentLot);
        child.setColdChainExcursion(parent.isColdChainExcursion());
        child.setStatus(parent.isColdChainExcursion() ? "QUARANTINED" : "AVAILABLE");
        lots.save(child);
        relations.save(new LotRelation(parentLot,childLotNumber,"REPACKAGING",childQuantity));
        return child;
    }

    @Transactional
    public Lot createSplit(String parentLot,int childQuantity,String childLotNumber){
        return repackage(parentLot,childQuantity,childLotNumber);
    }

    @Transactional
    public void merge(String newLotNumber,List<String> sourceLots,int quantity){
        if(lots.findByLotNumber(newLotNumber).isPresent()) throw new IllegalArgumentException("New lot already exists");
        int total=0; Lot template=null;
        for(String n:sourceLots){
            Lot l=getLot(n); template=l; total+=l.getAvailableQuantity();
        }
        if(quantity<=0 || quantity>total) throw new IllegalArgumentException("Invalid merge quantity");
        int remain=quantity;
        for(String n:sourceLots){
            if(remain<=0) break;
            Lot l=getLot(n); int take=Math.min(remain,l.getAvailableQuantity());
            l.setAvailableQuantity(l.getAvailableQuantity()-take);
            if(l.getAvailableQuantity()==0) l.setStatus("DEPLETED");
            lots.save(l); relations.save(new LotRelation(n,newLotNumber,"MERGE",take)); remain-=take;
        }
        Lot merged=new Lot(newLotNumber,template.getMedicationName(),template.getManufacturer(),
                template.getLocation(),quantity,template.getExpiryDate(),template.isColdChainRequired());
        merged.setStatus("AVAILABLE"); lots.save(merged);
    }

    public Map<String,Object> recallImpact(String rootLot){
        Set<String> impacted=new LinkedHashSet<>();
        Queue<String> q=new ArrayDeque<>(); q.add(rootLot);
        while(!q.isEmpty()){
            String current=q.poll();
            if(!impacted.add(current)) continue;
            for(LotRelation r:relations.findByParentLot(current)) q.add(r.getChildLot());
        }
        List<Dispensing> records=dispensing.findByLotNumberIn(impacted);
        Set<String> patientSet=new LinkedHashSet<>();
        Set<String> locationSet=new LinkedHashSet<>();
        List<Map<String,Object>> actions=new ArrayList<>();
        for(Dispensing d:records){
            patientSet.add(d.getPatientMrn()); locationSet.add(d.getLocation());
            actions.add(Map.of("action","CONTACT_PATIENT","patientMrn",d.getPatientMrn(),
                    "patientName",d.getPatientName(),"location",d.getLocation(),
                    "lotNumber",d.getLotNumber(),"quantity",d.getQuantity(),"dispensedAt",d.getDispensedAt()));
        }
        return Map.of("rootLot",rootLot,"impactedLots",impacted,"patientCount",patientSet.size(),
                "patients",patientSet,"locationCount",locationSet.size(),"locations",locationSet,
                "actions",actions);
    }

    public Lot getLot(String n){ return lots.findByLotNumber(n).orElseThrow(()->new IllegalArgumentException("Lot not found: "+n)); }
    public List<Lot> allLots(){return lots.findAll();}
    public List<Patient> allPatients(){return patients.findAll();}
}
