package com.csit321g4.miljuanat.skillpulse.entity;

import jakarta.persistence.*;

/** A skill/topic in the taxonomy. Optional parent gives a hierarchy (e.g. Algebra > Quadratics). */
@Entity
@Table(name = "skills")
public class Skill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    private Skill parent;

    public Skill() {}
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Skill getParent() { return parent; }
    public void setParent(Skill parent) { this.parent = parent; }
}
