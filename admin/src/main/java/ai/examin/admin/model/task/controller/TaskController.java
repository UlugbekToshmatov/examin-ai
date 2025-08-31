package ai.examin.admin.model.task.controller;

import ai.examin.admin.model.task.dto.*;
import ai.examin.admin.model.task.service.TaskService;
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
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;


    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getAll() {
        List<TaskResponseMin> tasks = taskService.getAll();

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("tasks", tasks))
                .build()
        );
    }

    @GetMapping("/all/by-program/{program-id}")
    public ResponseEntity<HttpResponse> getAllByProgram(@PathVariable("program-id") Long programId) {
        List<TaskResponseMin> tasks = taskService.getAllByCourseId(programId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("tasks", tasks))
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> get(@PathVariable Long id) {
        TaskResponse task = taskService.getById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("task", task))
                .build()
        );
    }

    @PostMapping()
    public ResponseEntity<HttpResponse> create(@RequestBody @Valid TaskRequest request) {
        TaskResponse taskResponse = taskService.create(request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .description(HttpStatus.CREATED.name())
                .data(Map.of("taskResponse", taskResponse))
                .build()
        );
    }

    @PreAuthorize(value = "hasAnyRole('SUPERVISOR', 'EXPERT')")
    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> update(@PathVariable Long id, @RequestBody @Valid TaskUpdateRequest request) {
        TaskResponse taskResponse = taskService.updateById(id, request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("taskResponse", taskResponse))
                .build()
        );
    }

    @PutMapping("/title/{id}")
    public ResponseEntity<HttpResponse> updateTitle(
        @PathVariable Long id, @RequestBody @Valid TaskUpdateTitleRequest request
    ) {
        TaskResponse taskResponse = taskService.updateTitle(id, request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("taskResponse", taskResponse))
                .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> delete(@PathVariable Long id) {
        taskService.deleteById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data("Task deleted successfully")
                .build()
        );
    }
}
