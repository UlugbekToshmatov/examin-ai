package ai.examin.admin.model.course.controller;

import ai.examin.admin.model.course.dto.CourseRequest;
import ai.examin.admin.model.course.dto.CourseResponse;
import ai.examin.admin.model.course.dto.CourseResponseMin;
import ai.examin.admin.model.course.dto.CourseUpdateRequest;
import ai.examin.admin.model.course.service.CourseService;
import ai.examin.core.base_classes.HttpResponse;
import ai.examin.core.enums.ResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                .data(Map.of("courses", courses))
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getById(@PathVariable Long id) {
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
        CourseResponse courseResponse = courseService.create(request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .description(HttpStatus.CREATED.name())
                .data(Map.of("courseResponse", courseResponse))
                .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> update(@PathVariable Long id, @RequestBody @Valid CourseUpdateRequest request) {
        CourseResponse courseResponse = courseService.updateById(id, request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("courseResponse", courseResponse))
                .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> delete(@PathVariable Long id) {
        courseService.delete(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data("Course deleted successfully")
                .build()
        );
    }
}
