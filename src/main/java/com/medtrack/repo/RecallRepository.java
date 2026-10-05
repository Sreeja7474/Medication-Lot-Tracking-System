package com.medtrack.repo;
import com.medtrack.model.Recall;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RecallRepository extends JpaRepository<Recall,Long>{ Optional<Recall> findByRecallNumber(String recallNumber); }
