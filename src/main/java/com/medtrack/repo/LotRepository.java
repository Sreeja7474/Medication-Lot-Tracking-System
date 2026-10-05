package com.medtrack.repo;
import com.medtrack.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LotRepository extends JpaRepository<Lot,Long>{
    Optional<Lot> findByLotNumber(String lotNumber);
    List<Lot> findByMedicationNameAndAvailableQuantityGreaterThanAndStatusOrderByExpiryDateAsc(String medication,int qty,String status);
}
