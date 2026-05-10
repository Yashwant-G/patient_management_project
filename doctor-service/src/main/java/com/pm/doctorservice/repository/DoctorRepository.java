package com.pm.doctorservice.repository;

import com.pm.doctorservice.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    /**
     * Searches for a doctor by matching all words in the input against the full_name (case-insensitive).
     * Examples:
     * "Dr Sarah Patel"  → matches "Dr. Sarah Patel"
     * "dr sarah"        → matches "Dr. Sarah Patel"
     * "dr patel"        → matches "Dr. Sarah Patel"
     */
    @Query(value = """
                    SELECT d.*, ts_rank_cd(vector, query) AS rank
                         FROM (
                             SELECT *, to_tsvector('english', full_name) AS vector
                             FROM doctor
                         ) d,
                         plainto_tsquery('english', :name) query
                         WHERE vector @@ query
                         ORDER BY rank DESC
                         LIMIT 1
             """, nativeQuery = true)
    Optional<Doctor> searchByName(@Param("name") String name);
}

