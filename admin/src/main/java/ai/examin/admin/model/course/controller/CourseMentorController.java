package ai.examin.admin.model.course.controller;

import ai.examin.admin.model.course.dto.CourseResponse;
import ai.examin.admin.model.course.service.CourseMentorService;
import ai.examin.core.base_classes.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-mentor")
@RequiredArgsConstructor
public class CourseMentorController {
    private final CourseMentorService courseMentorService;


    @PostMapping("/assign/{course-id}/{mentor-id}")
    public ResponseEntity<HttpResponse> assignMentor(
        @PathVariable("course-id") Long courseId,
        @PathVariable("mentor-id") UUID mentorId
    ) {
        CourseResponse course = courseMentorService.assignMentor(courseId, mentorId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("course", course))
                .build()
        );
    }

    @PutMapping("/unassign/{course-id}/{mentor-id}")
    public ResponseEntity<HttpResponse> unassignMentor(
        @PathVariable("course-id") Long courseId,
        @PathVariable("mentor-id") UUID mentorId
    ) {
        CourseResponse course = courseMentorService.unassignMentor(courseId, mentorId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("course", course))
                .build()
        );
    }
}
