package ai.examin.admin.model.course.controller;

import ai.examin.admin.model.course.dto.*;
import ai.examin.admin.model.course.service.CourseService;
import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.enums.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;


    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getAll() {
        List<CourseResponseMin> courses = courseService.getAll();

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("course", courses))
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> get(@PathVariable Long id) {
        CourseResponse course = courseService.getById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("course", course))
                .build()
        );
    }

    @PostMapping()
    public ResponseEntity<HttpResponse> create(@RequestBody @Valid CourseRequest request) {
        CourseResponse course = courseService.create(request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .description(HttpStatus.CREATED.name())
                .data(Map.of("course", course))
                .build()
        );
    }

    @PreAuthorize(value = "hasRole('SUPERVISOR')")
    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> update(@PathVariable Long id, @RequestBody @Valid CourseUpdateRequest request) {
        CourseResponse course = courseService.updateById(id, request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("course", course))
                .build()
        );
    }

    @PutMapping("/name/{id}")
    public ResponseEntity<HttpResponse> updateName(
        @PathVariable Long id, @RequestBody @Valid CourseUpdateNameRequest request
    ) {
        CourseResponse course = courseService.updateName(id, request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("course", course))
                .build()
        );
    }

    @PreAuthorize(value = "hasRole('SUPERVISOR')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<HttpResponse> approveCourse(@PathVariable Long id) {
        CourseResponse course = courseService.approveCourse(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("course", course))
                .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> delete(@PathVariable Long id) {
        courseService.deleteById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data("Course deleted successfully")
                .build()
        );
    }
}
