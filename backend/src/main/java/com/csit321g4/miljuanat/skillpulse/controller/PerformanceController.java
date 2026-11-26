package com.csit321g4.miljuanat.skillpulse.controller;

import com.csit321g4.miljuanat.skillpulse.dto.AssessmentDtos.*;
import com.csit321g4.miljuanat.skillpulse.entity.*;
import com.csit321g4.miljuanat.skillpulse.enums.Role;
import com.csit321g4.miljuanat.skillpulse.service.AuthGuard;
import com.csit321g4.miljuanat.skillpulse.service.SubmissionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Student submissions/mastery and instructor class analytics. */
@RestController
@RequestMapping("/api")
public class PerformanceController {
    private static final String AUTH = "Authorization";
    private final SubmissionService service;
    private final AuthGuard guard;

    public PerformanceController(SubmissionService service, AuthGuard guard) { this.service = service; this.guard = guard; }

    @PostMapping("/assessments/{id}/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponse submit(@RequestHeader(value = AUTH, required = false) String auth,
                                     @PathVariable Long id, @Valid @RequestBody SubmitRequest req) {
        return service.submit((Student) guard.require(auth, Role.STUDENT), id, req);
    }

    @GetMapping("/student/submissions")
    public List<SubmissionResponse> history(@RequestHeader(value = AUTH, required = false) String auth) {
        return service.history((Student) guard.require(auth, Role.STUDENT));
    }

    @GetMapping("/student/submissions/assessment/{assessmentId}")
    public SubmissionResponse result(@RequestHeader(value = AUTH, required = false) String auth,
                                     @PathVariable Long assessmentId) {
        return service.result((Student) guard.require(auth, Role.STUDENT), assessmentId);
    }

    @GetMapping("/student/mastery")
    public List<SkillMasteryResponse> mastery(@RequestHeader(value = AUTH, required = false) String auth) {
        return service.studentMastery((Student) guard.require(auth, Role.STUDENT));
    }

    @GetMapping("/instructor/analytics")
    public ClassAnalytics analytics(@RequestHeader(value = AUTH, required = false) String auth,
                                    @RequestParam(required = false) Double threshold) {
        return service.analytics((Instructor) guard.require(auth, Role.INSTRUCTOR), threshold);
    }
}
