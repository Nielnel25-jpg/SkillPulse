package com.csit321g4.miljuanat.skillpulse.repository;

import com.csit321g4.miljuanat.skillpulse.enums.Role;
import com.csit321g4.miljuanat.skillpulse.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    List<User> findByRoleOrderByFullNameAsc(Role role);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
