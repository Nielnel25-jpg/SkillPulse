package com.csit321g4.miljuanat.skillpulse.controller;

import com.csit321g4.miljuanat.skillpulse.dto.AssessmentDtos.*;
import com.csit321g4.miljuanat.skillpulse.enums.Role;
import com.csit321g4.miljuanat.skillpulse.service.AuthGuard;
import com.csit321g4.miljuanat.skillpulse.service.SkillService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skills")
public class SkillController {
    private final SkillService skills;
    private final AuthGuard guard;

    public SkillController(SkillService skills, AuthGuard guard) { this.skills = skills; this.guard = guard; }

    @GetMapping
    public List<SkillResponse> list(@RequestHeader(value = "Authorization", required = false) String auth) {
        guard.require(auth);
        return skills.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillResponse create(@RequestHeader(value = "Authorization", required = false) String auth,
                                @Valid @RequestBody SkillRequest req) {
        guard.require(auth, Role.ADMIN, Role.INSTRUCTOR);
        return skills.create(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long id) {
        guard.require(auth, Role.ADMIN);
        skills.delete(id);
    }
}
