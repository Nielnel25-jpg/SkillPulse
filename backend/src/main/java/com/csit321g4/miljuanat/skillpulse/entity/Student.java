package com.csit321g4.miljuanat.skillpulse.entity;

import com.csit321g4.miljuanat.skillpulse.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student extends User {

    private String studentNumber;

    public Student() { super(Role.STUDENT); }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
}
