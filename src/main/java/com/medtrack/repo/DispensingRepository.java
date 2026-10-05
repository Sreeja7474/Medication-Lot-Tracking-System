package com.medtrack.repo;
import com.medtrack.model.Dispensing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface DispensingRepository extends JpaRepository<Dispensing,Long>{
    List<Dispensing> findByLotNumberIn(Collection<String> lots);
}
