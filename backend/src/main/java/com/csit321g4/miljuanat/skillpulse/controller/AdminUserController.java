package com.csit321g4.miljuanat.skillpulse.controller;

import com.csit321g4.miljuanat.skillpulse.dto.UserDtos.CreateUserRequest;
import com.csit321g4.miljuanat.skillpulse.dto.UserDtos.UpdateUserRequest;
import com.csit321g4.miljuanat.skillpulse.dto.UserDtos.UserResponse;
import com.csit321g4.miljuanat.skillpulse.enums.Role;
import com.csit321g4.miljuanat.skillpulse.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** User management endpoints (security is not added yet in this increment). */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) { this.userService = userService; }

    /** POST /api/admin/users - create a Student, Instructor or Admin account. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest req) { return userService.create(req); }

    /** GET /api/admin/users?role=STUDENT - list users, optionally by role. */
    @GetMapping
    public List<UserResponse> list(@RequestParam(required = false) Role role) { return userService.list(role); }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) { return userService.get(id); }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        return userService.update(id, req);
    }

    /** DELETE = deactivate (soft delete) so historical responses stay intact. */
    @DeleteMapping("/{id}")
    public UserResponse deactivate(@PathVariable Long id) {
        return userService.setActive(id, false);
    }

    /** DELETE /api/admin/users/{id}/permanent - removes the user completely (cannot be undone). */
    @DeleteMapping("/{id}/permanent")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermanently(@PathVariable Long id) {
        userService.deletePermanently(id);
    }

    @PatchMapping("/{id}/activate")
    public UserResponse activate(@PathVariable Long id) {
        return userService.setActive(id, true);
    }
}
