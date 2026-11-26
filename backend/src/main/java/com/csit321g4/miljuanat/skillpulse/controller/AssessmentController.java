package com.csit321g4.miljuanat.skillpulse.controller;

import com.csit321g4.miljuanat.skillpulse.dto.AssessmentDtos.*;
import com.csit321g4.miljuanat.skillpulse.entity.*;
import com.csit321g4.miljuanat.skillpulse.enums.Role;
import com.csit321g4.miljuanat.skillpulse.service.AssessmentService;
import com.csit321g4.miljuanat.skillpulse.service.AuthGuard;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {
    private static final String AUTH = "Authorization";
    private final AssessmentService service;
    private final AuthGuard guard;

    public AssessmentController(AssessmentService service, AuthGuard guard) { this.service = service; this.guard = guard; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentResponse create(@RequestHeader(value = AUTH, required = false) String auth,
                                     @Valid @RequestBody CreateAssessmentRequest req) {
        return service.create((Instructor) guard.require(auth, Role.INSTRUCTOR), req);
    }

    @GetMapping("/mine")
    public List<AssessmentResponse> mine(@RequestHeader(value = AUTH, required = false) String auth) {
        return service.listMine((Instructor) guard.require(auth, Role.INSTRUCTOR));
    }

    @GetMapping("/available")
    public List<AssessmentResponse> available(@RequestHeader(value = AUTH, required = false) String auth) {
        return service.listAvailable((Student) guard.require(auth, Role.STUDENT));
    }

    @GetMapping("/{id}")
    public AssessmentResponse get(@RequestHeader(value = AUTH, required = false) String auth, @PathVariable Long id) {
        return service.get(guard.require(auth, Role.INSTRUCTOR, Role.STUDENT), id);
    }

    @PostMapping("/{id}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentResponse addQuestion(@RequestHeader(value = AUTH, required = false) String auth,
                                          @PathVariable Long id, @Valid @RequestBody QuestionRequest req) {
        return service.addQuestion((Instructor) guard.require(auth, Role.INSTRUCTOR), id, req);
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    public AssessmentResponse removeQuestion(@RequestHeader(value = AUTH, required = false) String auth,
                                             @PathVariable Long id, @PathVariable Long questionId) {
        return service.removeQuestion((Instructor) guard.require(auth, Role.INSTRUCTOR), id, questionId);
    }

    @PatchMapping("/{id}/status")
    public AssessmentResponse setStatus(@RequestHeader(value = AUTH, required = false) String auth,
                                        @PathVariable Long id, @Valid @RequestBody StatusRequest req) {
        return service.setStatus((Instructor) guard.require(auth, Role.INSTRUCTOR), id, req.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(value = AUTH, required = false) String auth, @PathVariable Long id) {
        service.delete((Instructor) guard.require(auth, Role.INSTRUCTOR), id);
    }
}
