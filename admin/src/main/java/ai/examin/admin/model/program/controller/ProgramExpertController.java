package ai.examin.admin.model.program.controller;

import ai.examin.admin.model.program.dto.ProgramResponse;
import ai.examin.admin.model.program.service.ProgramExpertService;
import ai.examin.core.base_classes.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/program-expert")
@RequiredArgsConstructor
public class ProgramExpertController {
    private final ProgramExpertService programExpertService;


    @PostMapping("/assign/{program-id}/{expert-id}")
    public ResponseEntity<HttpResponse> assignExpert(
        @PathVariable("program-id") Long programId,
        @PathVariable("expert-id") UUID expertId
    ) {
        ProgramResponse program = programExpertService.assignExpert(programId, expertId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("program", program))
                .build()
        );
    }

    @PutMapping("/unassign/{program-id}/{expert-id}")
    public ResponseEntity<HttpResponse> unassignExpert(
        @PathVariable("program-id") Long programId,
        @PathVariable("expert-id") UUID expertId
    ) {
        ProgramResponse program = programExpertService.unassignExpert(programId, expertId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("program", program))
                .build()
        );
    }
}
