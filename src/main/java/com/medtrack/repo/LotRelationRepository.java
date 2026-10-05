package com.medtrack.repo;
import com.medtrack.model.LotRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LotRelationRepository extends JpaRepository<LotRelation,Long>{
    List<LotRelation> findByParentLot(String parentLot);
    List<LotRelation> findByChildLot(String childLot);
}
