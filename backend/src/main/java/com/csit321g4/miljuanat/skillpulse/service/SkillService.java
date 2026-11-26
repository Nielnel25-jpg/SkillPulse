package com.csit321g4.miljuanat.skillpulse.service;

import com.csit321g4.miljuanat.skillpulse.dto.AssessmentDtos.*;
import com.csit321g4.miljuanat.skillpulse.entity.Skill;
import com.csit321g4.miljuanat.skillpulse.exception.ApiException;
import com.csit321g4.miljuanat.skillpulse.repository.QuestionRepository;
import com.csit321g4.miljuanat.skillpulse.repository.SkillRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SkillService {
    private final SkillRepository skills;
    private final QuestionRepository questions;

    public SkillService(SkillRepository skills, QuestionRepository questions) {
        this.skills = skills;
        this.questions = questions;
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> list() {
        return skills.findAllByOrderByNameAsc().stream().map(this::toResponse).toList();
    }

    public SkillResponse create(SkillRequest req) {
        String name = req.name().trim();
        if (skills.existsByNameIgnoreCase(name)) throw ApiException.conflict("Skill already exists: " + name);
        Skill s = new Skill();
        s.setName(name);
        s.setDescription(req.description());
        if (req.parentId() != null) {
            s.setParent(skills.findById(req.parentId()).orElseThrow(() -> ApiException.notFound("Parent skill not found")));
        }
        return toResponse(skills.save(s));
    }

    public void delete(Long id) {
        if (!skills.existsById(id)) throw ApiException.notFound("Skill not found");
        if (questions.existsBySkillId(id)) throw ApiException.conflict("Skill is used by questions and cannot be deleted");
        if (skills.existsByParentId(id)) throw ApiException.conflict("Skill has child skills and cannot be deleted");
        skills.deleteById(id);
    }

    private SkillResponse toResponse(Skill s) {
        return new SkillResponse(s.getId(), s.getName(), s.getDescription(),
                s.getParent() == null ? null : s.getParent().getId());
    }
}
