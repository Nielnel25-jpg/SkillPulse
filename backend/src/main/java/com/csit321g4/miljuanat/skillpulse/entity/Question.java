package com.csit321g4.miljuanat.skillpulse.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Multiple-choice question tagged with one skill; points act as its weight in mastery. */
@Entity
@Table(name = "questions")
public class Question {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Assessment assessment;
    @Column(nullable = false, length = 1000)
    private String text;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options")
    @OrderColumn(name = "position")
    @Column(name = "option_text")
    private List<String> options = new ArrayList<>();
    @Column(nullable = false)
    private int correctIndex;
    @Column(nullable = false)
    private int points = 1;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Skill skill;

    public Question() {}
    public Long getId() { return id; }
    public Assessment getAssessment() { return assessment; }
    public void setAssessment(Assessment assessment) { this.assessment = assessment; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = new ArrayList<>(options); }
    public int getCorrectIndex() { return correctIndex; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }
}
