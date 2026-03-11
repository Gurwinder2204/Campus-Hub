package com.campusstudyhub.repository;

import com.campusstudyhub.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Subject entity operations.
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * Find all subjects for a given semester.
     * 
     * @param semesterId the semester ID
     * @return list of subjects
     */
    List<Subject> findBySemesterId(Long semesterId);

    /**
     * Find all subjects for a given semester number.
     * 
     * @param semesterNumber the semester number (1-8)
     * @return list of subjects
     */
    @Query("SELECT s FROM Subject s WHERE s.semester.number = :semesterNumber")
    List<Subject> findBySemesterNumber(@Param("semesterNumber") Integer semesterNumber);

    /**
     * Search subjects by name containing the query (case insensitive).
     * 
     * @param query the search query
     * @return list of matching subjects
     */
    List<Subject> findByNameContainingIgnoreCase(String query);

    /**
     * Find subject by name and semester.
     * 
     * @param name       the subject name
     * @param semesterId the semester ID
     * @return list of subjects matching criteria
     */
    List<Subject> findByNameAndSemesterId(String name, Long semesterId);

    @EntityGraph(attributePaths = "semester")
    java.util.Optional<Subject> findDetailedById(Long id);
}
