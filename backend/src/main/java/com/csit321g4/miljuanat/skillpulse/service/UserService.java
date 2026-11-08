package com.csit321g4.miljuanat.skillpulse.service;

import com.csit321g4.miljuanat.skillpulse.dto.UserDtos.CreateUserRequest;
import com.csit321g4.miljuanat.skillpulse.dto.UserDtos.UpdateUserRequest;
import com.csit321g4.miljuanat.skillpulse.dto.UserDtos.UserResponse;
import com.csit321g4.miljuanat.skillpulse.exception.ApiException;
import com.csit321g4.miljuanat.skillpulse.entity.Admin;
import com.csit321g4.miljuanat.skillpulse.entity.Instructor;
import com.csit321g4.miljuanat.skillpulse.enums.Role;
import com.csit321g4.miljuanat.skillpulse.entity.Student;
import com.csit321g4.miljuanat.skillpulse.entity.User;
import com.csit321g4.miljuanat.skillpulse.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public UserResponse create(CreateUserRequest req) {
        if (users.existsByEmail(req.email())) throw ApiException.conflict("Email already in use");
        User user = switch (req.role()) {
            case STUDENT -> {
                Student s = new Student();
                s.setStudentNumber(req.studentNumber());
                yield s;
            }
            case INSTRUCTOR -> {
                Instructor i = new Instructor();
                i.setDepartment(req.department());
                yield i;
            }
            case ADMIN -> new Admin();
        };
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setPasswordHash(encoder.encode(req.password()));
        return toResponse(users.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list(Role role) {
        List<User> found = role == null ? users.findAll() : users.findByRoleOrderByFullNameAsc(role);
        return found.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return toResponse(find(id));
    }

    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = find(id);
        if (req.fullName() != null && !req.fullName().isBlank()) user.setFullName(req.fullName());
        if (req.email() != null && !req.email().equalsIgnoreCase(user.getEmail())) {
            if (users.existsByEmail(req.email())) throw ApiException.conflict("Email already in use");
            user.setEmail(req.email());
        }
        if (req.password() != null) user.setPasswordHash(encoder.encode(req.password()));
        if (user instanceof Student s && req.studentNumber() != null) s.setStudentNumber(req.studentNumber());
        if (user instanceof Instructor i && req.department() != null) i.setDepartment(req.department());
        return toResponse(user);
    }

    public UserResponse setActive(Long id, boolean active) {
        User user = find(id);
        user.setActive(active);
        return toResponse(user);
    }

    /** Removes the user (and the matching student/instructor row) completely. Cannot be undone. */
    public void deletePermanently(Long id) {
        User user = find(id);
        users.delete(user);
    }

    private User find(Long id) {
        return users.findById(id).orElseThrow(() -> ApiException.notFound("User " + id + " not found"));
    }

    private UserResponse toResponse(User u) {
        String studentNumber = u instanceof Student s ? s.getStudentNumber() : null;
        String department = u instanceof Instructor i ? i.getDepartment() : null;
        return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.isActive(),
                u.getCreatedAt(), studentNumber, department);
    }
}
