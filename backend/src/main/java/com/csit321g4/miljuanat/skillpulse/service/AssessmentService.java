package com.csit321g4.miljuanat.skillpulse.service;

import com.csit321g4.miljuanat.skillpulse.dto.AssessmentDtos.*;
import com.csit321g4.miljuanat.skillpulse.entity.*;
import com.csit321g4.miljuanat.skillpulse.enums.AssessmentStatus;
import com.csit321g4.miljuanat.skillpulse.enums.Role;
import com.csit321g4.miljuanat.skillpulse.exception.ApiException;
import com.csit321g4.miljuanat.skillpulse.repository.*;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AssessmentService {
    private final AssessmentRepository assessments;
    private final SkillRepository skills;
    private final SubmissionRepository submissions;

    public AssessmentService(AssessmentRepository assessments, SkillRepository skills, SubmissionRepository submissions) {
        this.assessments = assessments;
        this.skills = skills;
        this.submissions = submissions;
    }

    public AssessmentResponse create(Instructor owner, CreateAssessmentRequest req) {
        Assessment a = new Assessment();
        a.setTitle(req.title().trim());
        a.setDescription(req.description());
        a.setDueAt(req.dueAt());
        a.setTimeLimitMinutes(req.timeLimitMinutes());
        a.setInstructor(owner);
        return toResponse(assessments.save(a), true, null);
    }

    @Transactional(readOnly = true)
    public List<AssessmentResponse> listMine(Instructor owner) {
        return assessments.findByInstructorIdOrderByCreatedAtDesc(owner.getId()).stream()
                .map(a -> toResponse(a, false, null)).toList();
    }

    /** Published assessments for students, flagged with whether this student already submitted. */
    @Transactional(readOnly = true)
    public List<AssessmentResponse> listAvailable(Student student) {
        return assessments.findByStatusOrderByCreatedAtDesc(AssessmentStatus.PUBLISHED).stream()
                .map(a -> toResponse(a, false, submissions.existsByAssessmentIdAndStudentId(a.getId(), student.getId())))
                .toList();
    }

    /** Instructors (owner only) see answer keys; students only see published assessments without them. */
    @Transactional(readOnly = true)
    public AssessmentResponse get(User viewer, Long id) {
        Assessment a = find(id);
        if (viewer.getRole() == Role.STUDENT) {
            if (a.getStatus() != AssessmentStatus.PUBLISHED) throw ApiException.notFound("Assessment not found");
            return toResponse(a, true, submissions.existsByAssessmentIdAndStudentId(id, viewer.getId()));
        }
        requireOwner(viewer, a);
        return toResponse(a, true, null);
    }

    public AssessmentResponse addQuestion(Instructor owner, Long id, QuestionRequest req) {
        Assessment a = find(id);
        requireOwner(owner, a);
        if (a.getStatus() != AssessmentStatus.DRAFT) throw ApiException.conflict("Only draft assessments can be edited");
        if (req.correctIndex() >= req.options().size()) throw ApiException.badRequest("correctIndex is out of range");
        Question q = new Question();
        q.setAssessment(a);
        q.setText(req.text().trim());
        q.setOptions(req.options().stream().map(String::trim).toList());
        q.setCorrectIndex(req.correctIndex());
        q.setPoints(req.points() == null ? 1 : req.points());
        q.setSkill(skills.findById(req.skillId()).orElseThrow(() -> ApiException.notFound("Skill not found")));
        a.getQuestions().add(q);
        return toResponse(assessments.save(a), true, null);
    }

    public AssessmentResponse removeQuestion(Instructor owner, Long id, Long questionId) {
        Assessment a = find(id);
        requireOwner(owner, a);
        if (a.getStatus() != AssessmentStatus.DRAFT) throw ApiException.conflict("Only draft assessments can be edited");
        if (!a.getQuestions().removeIf(q -> q.getId().equals(questionId))) throw ApiException.notFound("Question not found");
        return toResponse(a, true, null);
    }

    public AssessmentResponse setStatus(Instructor owner, Long id, AssessmentStatus status) {
        Assessment a = find(id);
        requireOwner(owner, a);
        if (status == AssessmentStatus.DRAFT && a.getStatus() != AssessmentStatus.DRAFT) {
            throw ApiException.conflict("An assessment cannot go back to draft");
        }
        if (status == AssessmentStatus.PUBLISHED && a.getQuestions().isEmpty()) {
            throw ApiException.badRequest("Add at least one question before publishing");
        }
        a.setStatus(status);
        return toResponse(a, true, null);
    }

    public void delete(Instructor owner, Long id) {
        Assessment a = find(id);
        requireOwner(owner, a);
        if (a.getStatus() != AssessmentStatus.DRAFT) throw ApiException.conflict("Only draft assessments can be deleted");
        assessments.delete(a);
    }

    private Assessment find(Long id) {
        return assessments.findById(id).orElseThrow(() -> ApiException.notFound("Assessment not found"));
    }

    private void requireOwner(User viewer, Assessment a) {
        if (viewer.getRole() != Role.INSTRUCTOR || !a.getInstructor().getId().equals(viewer.getId())) {
            throw new ApiException(org.springframework.http.HttpStatus.FORBIDDEN, "You do not own this assessment");
        }
    }

    private AssessmentResponse toResponse(Assessment a, boolean withQuestions, Boolean submitted) {
        boolean showKey = submitted == null; // instructor view
        List<QuestionResponse> qs = withQuestions
                ? a.getQuestions().stream().map(q -> new QuestionResponse(q.getId(), q.getText(), q.getOptions(),
                        showKey ? q.getCorrectIndex() : null, q.getPoints(), q.getSkill().getId(), q.getSkill().getName())).toList()
                : null;
        return new AssessmentResponse(a.getId(), a.getTitle(), a.getDescription(), a.getStatus(), a.getDueAt(),
                a.getTimeLimitMinutes(), a.getInstructor().getFullName(), a.getQuestions().size(), submitted, qs);
    }
}
