package com.csit321g4.miljuanat.skillpulse.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "submissions", uniqueConstraints = @UniqueConstraint(columnNames = {"assessment_id", "student_id"}))
public class Submission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Assessment assessment;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Student student;
    @Column(nullable = false)
    private Instant submittedAt = Instant.now();
    private int score;
    private int maxScore;
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    public Submission() {}
    public Long getId() { return id; }
    public Assessment getAssessment() { return assessment; }
    public void setAssessment(Assessment assessment) { this.assessment = assessment; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Instant getSubmittedAt() { return submittedAt; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }
    public List<Answer> getAnswers() { return answers; }
}
