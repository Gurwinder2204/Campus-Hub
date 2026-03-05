package com.campusstudyhub.repository;

import com.campusstudyhub.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findAllByOrderByCreatedAtDesc();

    List<Complaint> findBySubmittedByOrderByCreatedAtDesc(String submittedBy);

    List<Complaint> findByStatusOrderByCreatedAtDesc(String status);
}
