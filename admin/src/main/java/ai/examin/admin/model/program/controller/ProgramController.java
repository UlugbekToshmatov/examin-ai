package ai.examin.admin.model.program.controller;

import ai.examin.admin.model.program.dto.ProgramRequest;
import ai.examin.admin.model.program.dto.ProgramResponse;
import ai.examin.admin.model.program.dto.ProgramResponseMin;
import ai.examin.admin.model.program.dto.ProgramUpdateRequest;
import ai.examin.admin.model.program.service.ProgramService;
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
@RequestMapping("/api/v1/program")
@RequiredArgsConstructor
public class ProgramController {
    private final ProgramService programService;


    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getAll() {
        List<ProgramResponseMin> programs = programService.getAll();

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("program", programs))
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getById(@PathVariable Long id) {
        ProgramResponse program = programService.getById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("program", program))
                .build()
        );
    }

    @PostMapping()
    public ResponseEntity<HttpResponse> create(@RequestBody @Valid ProgramRequest request) {
        ProgramResponse program = programService.create(request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .description(HttpStatus.CREATED.name())
                .data(Map.of("program", program))
                .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> update(@PathVariable Long id, @RequestBody @Valid ProgramUpdateRequest request) {
        ProgramResponse program = programService.updateById(id, request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("program", program))
                .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> delete(@PathVariable Long id) {
        programService.deleteById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data("Program deleted successfully")
                .build()
        );
    }
}
