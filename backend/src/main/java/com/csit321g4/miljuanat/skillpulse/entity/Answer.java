package com.csit321g4.miljuanat.skillpulse.entity;

import jakarta.persistence.*;

/** One row per question per submission (selectedIndex null = skipped) so mastery denominators are complete. */
@Entity
@Table(name = "answers")
public class Answer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Submission submission;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Question question;
    private Integer selectedIndex;
    private boolean correct;
    private int pointsAwarded;

    public Answer() {}
    public Long getId() { return id; }
    public Submission getSubmission() { return submission; }
    public void setSubmission(Submission submission) { this.submission = submission; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public Integer getSelectedIndex() { return selectedIndex; }
    public void setSelectedIndex(Integer selectedIndex) { this.selectedIndex = selectedIndex; }
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public int getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(int pointsAwarded) { this.pointsAwarded = pointsAwarded; }
}
