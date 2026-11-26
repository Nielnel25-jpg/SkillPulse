package com.csit321g4.miljuanat.skillpulse.service;

import com.csit321g4.miljuanat.skillpulse.dto.AssessmentDtos.*;
import com.csit321g4.miljuanat.skillpulse.entity.*;
import com.csit321g4.miljuanat.skillpulse.enums.AssessmentStatus;
import com.csit321g4.miljuanat.skillpulse.exception.ApiException;
import com.csit321g4.miljuanat.skillpulse.repository.*;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubmissionService {
    private static final double DEFAULT_THRESHOLD = 60.0;

    private final AssessmentRepository assessments;
    private final SubmissionRepository submissions;
    private final AnswerRepository answers;

    public SubmissionService(AssessmentRepository assessments, SubmissionRepository submissions, AnswerRepository answers) {
        this.assessments = assessments;
        this.submissions = submissions;
        this.answers = answers;
    }

    /** Auto-grades a multiple-choice submission. One submission per student per assessment. */
    public SubmissionResponse submit(Student student, Long assessmentId, SubmitRequest req) {
        Assessment a = assessments.findById(assessmentId).orElseThrow(() -> ApiException.notFound("Assessment not found"));
        if (a.getStatus() != AssessmentStatus.PUBLISHED) throw ApiException.conflict("This assessment is not open");
        if (a.getDueAt() != null && Instant.now().isAfter(a.getDueAt())) throw ApiException.conflict("The due date has passed");
        if (submissions.existsByAssessmentIdAndStudentId(assessmentId, student.getId())) {
            throw ApiException.conflict("You have already submitted this assessment");
        }
        Map<Long, Integer> picked = new HashMap<>();
        for (AnswerItem item : req.answers()) picked.put(item.questionId(), item.selectedIndex());
        for (Long qid : picked.keySet()) {
            if (a.getQuestions().stream().noneMatch(q -> q.getId().equals(qid))) {
                throw ApiException.badRequest("Question " + qid + " does not belong to this assessment");
            }
        }

        Submission sub = new Submission();
        sub.setAssessment(a);
        sub.setStudent(student);
        int score = 0, max = 0;
        for (Question q : a.getQuestions()) {
            Integer sel = picked.get(q.getId());
            boolean ok = sel != null && sel == q.getCorrectIndex();
            Answer ans = new Answer();
            ans.setSubmission(sub);
            ans.setQuestion(q);
            ans.setSelectedIndex(sel);
            ans.setCorrect(ok);
            ans.setPointsAwarded(ok ? q.getPoints() : 0);
            sub.getAnswers().add(ans);
            score += ans.getPointsAwarded();
            max += q.getPoints();
        }
        sub.setScore(score);
        sub.setMaxScore(max);
        return toResponse(submissions.save(sub), true);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> history(Student student) {
        return submissions.findByStudentIdOrderBySubmittedAtDesc(student.getId()).stream()
                .map(s -> toResponse(s, false)).toList();
    }

    @Transactional(readOnly = true)
    public SubmissionResponse result(Student student, Long assessmentId) {
        Submission s = submissions.findByAssessmentIdAndStudentId(assessmentId, student.getId())
                .orElseThrow(() -> ApiException.notFound("No submission found"));
        return toResponse(s, true);
    }

    /** Student mastery per skill, weakest first. */
    @Transactional(readOnly = true)
    public List<SkillMasteryResponse> studentMastery(Student student) {
        return toMastery(answers.masteryByStudent(student.getId()));
    }

    /** Class analytics for an instructor: weakest skills first, plus students below the at-risk threshold. */
    @Transactional(readOnly = true)
    public ClassAnalytics analytics(Instructor instructor, Double threshold) {
        double t = threshold == null ? DEFAULT_THRESHOLD : threshold;
        List<StudentSummary> students = answers.overallByStudentForInstructor(instructor.getId()).stream()
                .map(r -> {
                    long earned = ((Number) r[2]).longValue(), possible = ((Number) r[3]).longValue();
                    double pct = pct(earned, possible);
                    return new StudentSummary((Long) r[0], (String) r[1], earned, possible, pct, pct < t);
                })
                .sorted(Comparator.comparingDouble(StudentSummary::percent))
                .toList();
        return new ClassAnalytics(toMastery(answers.masteryByInstructor(instructor.getId())), students, t,
                submissions.countByAssessmentInstructorId(instructor.getId()));
    }

    private List<SkillMasteryResponse> toMastery(List<Object[]> rows) {
        return rows.stream().map(r -> {
            long earned = ((Number) r[2]).longValue(), possible = ((Number) r[3]).longValue();
            return new SkillMasteryResponse((Long) r[0], (String) r[1], earned, possible, pct(earned, possible));
        }).sorted(Comparator.comparingDouble(SkillMasteryResponse::percent)).toList();
    }

    private static double pct(long earned, long possible) {
        return possible == 0 ? 0 : Math.round(earned * 1000.0 / possible) / 10.0;
    }

    private SubmissionResponse toResponse(Submission s, boolean withReview) {
        List<ReviewItem> review = withReview
                ? s.getAnswers().stream().map(x -> new ReviewItem(x.getQuestion().getId(), x.getQuestion().getText(),
                        x.getQuestion().getSkill().getName(), x.getSelectedIndex(), x.getQuestion().getCorrectIndex(),
                        x.isCorrect(), x.getPointsAwarded(), x.getQuestion().getPoints())).toList()
                : null;
        return new SubmissionResponse(s.getId(), s.getAssessment().getId(), s.getAssessment().getTitle(), s.getScore(),
                s.getMaxScore(), pct(s.getScore(), s.getMaxScore()), s.getSubmittedAt(), review);
    }
}
