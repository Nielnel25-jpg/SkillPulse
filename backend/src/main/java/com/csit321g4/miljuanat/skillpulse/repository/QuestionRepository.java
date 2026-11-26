package com.csit321g4.miljuanat.skillpulse.repository;

import com.csit321g4.miljuanat.skillpulse.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    boolean existsBySkillId(Long skillId);
}
