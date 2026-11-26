package com.csit321g4.miljuanat.skillpulse.repository;

import com.csit321g4.miljuanat.skillpulse.entity.Skill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    boolean existsByNameIgnoreCase(String name);
    List<Skill> findAllByOrderByNameAsc();
    boolean existsByParentId(Long parentId);
}
