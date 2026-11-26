package com.csit321g4.miljuanat.skillpulse.dto;

import com.csit321g4.miljuanat.skillpulse.enums.AssessmentStatus;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;

public final class AssessmentDtos {
    private AssessmentDtos() {}

    public record SkillRequest(@NotBlank String name, String description, Long parentId) {}
    public record SkillResponse(Long id, String name, String description, Long parentId) {}

    public record CreateAssessmentRequest(@NotBlank String title, String description,
                                          Instant dueAt, @Min(1) Integer timeLimitMinutes) {}
    public record StatusRequest(@NotNull AssessmentStatus status) {}

    public record QuestionRequest(@NotBlank String text,
                                  @NotNull @Size(min = 2, max = 6) List<@NotBlank String> options,
                                  @NotNull @Min(0) Integer correctIndex,
                                  @Min(1) Integer points,
                                  @NotNull Long skillId) {}
    /** correctIndex is null when the viewer is a student. */
    public record QuestionResponse(Long id, String text, List<String> options, Integer correctIndex,
                                   int points, Long skillId, String skillName) {}

    public record AssessmentResponse(Long id, String title, String description, AssessmentStatus status,
                                     Instant dueAt, Integer timeLimitMinutes, String instructorName,
                                     int questionCount, Boolean submitted, List<QuestionResponse> questions) {}

    public record AnswerItem(@NotNull Long questionId, Integer selectedIndex) {}
    public record SubmitRequest(@NotNull List<AnswerItem> answers) {}

    public record ReviewItem(Long questionId, String text, String skillName, Integer selectedIndex,
                             int correctIndex, boolean correct, int pointsAwarded, int points) {}
    public record SubmissionResponse(Long id, Long assessmentId, String assessmentTitle, int score,
                                     int maxScore, double percent, Instant submittedAt, List<ReviewItem> review) {}

    public record SkillMasteryResponse(Long skillId, String skillName, long earned, long possible, double percent) {}
    public record StudentSummary(Long studentId, String fullName, long earned, long possible,
                                 double percent, boolean atRisk) {}
    /** skills are sorted weakest-first. */
    public record ClassAnalytics(List<SkillMasteryResponse> skills, List<StudentSummary> students,
                                 double threshold, long submissions) {}
}
