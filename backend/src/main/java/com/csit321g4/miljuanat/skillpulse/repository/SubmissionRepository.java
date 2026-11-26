package com.csit321g4.miljuanat.skillpulse.repository;

import com.csit321g4.miljuanat.skillpulse.entity.Submission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByAssessmentIdAndStudentId(Long assessmentId, Long studentId);
    Optional<Submission> findByAssessmentIdAndStudentId(Long assessmentId, Long studentId);
    List<Submission> findByStudentIdOrderBySubmittedAtDesc(Long studentId);
    long countByAssessmentInstructorId(Long instructorId);
}
