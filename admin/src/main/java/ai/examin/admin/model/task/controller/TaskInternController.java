package ai.examin.admin.model.task.controller;

import ai.examin.admin.model.task.dto.TaskResponse;
import ai.examin.admin.model.task.service.TaskInternService;
import ai.examin.core.base_classes.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/task-intern")
@RequiredArgsConstructor
public class TaskInternController {
    private final TaskInternService taskInternService;


    @PostMapping("/assign/{task-id}/{intern-id}")
    public ResponseEntity<HttpResponse> assignIntern(
        @PathVariable("task-id") Long taskId,
        @PathVariable("intern-id") UUID internId
    ) {
        TaskResponse task = taskInternService.assignIntern(taskId, internId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("task", task))
                .build()
        );
    }

    @PutMapping("/unassign/{task-id}/{intern-id}")
    public ResponseEntity<HttpResponse> unassignIntern(
        @PathVariable("task-id") Long taskId,
        @PathVariable("intern-id") UUID internId
    ) {
        TaskResponse task = taskInternService.unassignIntern(taskId, internId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("task", task))
                .build()
        );
    }
}
