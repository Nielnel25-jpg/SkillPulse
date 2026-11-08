package com.csit321g4.miljuanat.skillpulse.entity;

import com.csit321g4.miljuanat.skillpulse.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends User {
    public Admin() { super(Role.ADMIN); }
}
