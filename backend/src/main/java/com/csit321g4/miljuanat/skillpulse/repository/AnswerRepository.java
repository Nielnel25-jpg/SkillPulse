package com.csit321g4.miljuanat.skillpulse.repository;

import com.csit321g4.miljuanat.skillpulse.entity.Answer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Mastery = SUM(points awarded) / SUM(points possible), aggregated in the database. */
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    /** rows: [skillId, skillName, earned, possible] for one student. */
    @Query("""
        select q.skill.id, q.skill.name, sum(a.pointsAwarded), sum(q.points)
        from Answer a join a.question q
        where a.submission.student.id = :studentId
        group by q.skill.id, q.skill.name
        """)
    List<Object[]> masteryByStudent(@Param("studentId") Long studentId);

    /** rows: [skillId, skillName, earned, possible] across all students of an instructor's assessments. */
    @Query("""
        select q.skill.id, q.skill.name, sum(a.pointsAwarded), sum(q.points)
        from Answer a join a.question q
        where q.assessment.instructor.id = :instructorId
        group by q.skill.id, q.skill.name
        """)
    List<Object[]> masteryByInstructor(@Param("instructorId") Long instructorId);

    /** rows: [studentId, fullName, earned, possible] per student of an instructor's assessments. */
    @Query("""
        select s.id, s.fullName, sum(a.pointsAwarded), sum(q.points)
        from Answer a join a.question q join a.submission sub join sub.student s
        where q.assessment.instructor.id = :instructorId
        group by s.id, s.fullName
        """)
    List<Object[]> overallByStudentForInstructor(@Param("instructorId") Long instructorId);
}
