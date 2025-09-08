package ai.examin.admin.model.program.service;

import ai.examin.admin.model.program.dto.ProgramRequest;
import ai.examin.admin.model.program.dto.ProgramResponse;
import ai.examin.admin.model.program.dto.ProgramResponseMin;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.entity.ProgramExpert;
import ai.examin.admin.model.program.repository.ProgramExpertRepository;
import ai.examin.admin.model.program.repository.ProgramRepository;
import ai.examin.core.enums.ProgramStatus;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.exception_handler.ApiException;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ai.examin.admin.model.program.mapper.ProgramMapper.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;
    private final ProgramExpertRepository programExpertRepository;
    private final UserContextService userContextService;


    public List<ProgramResponseMin> getAll() {
        return toResponseList(programRepository.findAllByStatusNot(ProgramStatus.DELETED));
    }

    public ProgramResponse getById(Long id) {
        Program program = programRepository.findByIdAndStatusNot(id, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        List<ProgramExpert> experts = programExpertRepository.findAllByProgramId(program.getId());

        return toResponse(program, experts);
    }

    public ProgramResponse create(ProgramRequest request) {
        if (programRepository.existsByNameAndStatusNot(request.name(), ProgramStatus.DELETED))
            throw new ApiException(ResponseStatus.PROGRAM_ALREADY_EXISTS);

        Program program = programRepository.save(toEntity(request));
        return toResponse(program, List.of());
    }

    public ProgramResponse updateById(Long id, ProgramRequest request) {
        Program program = programRepository.findByIdAndStatusNot(id, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        if (programRepository.existsByNameAndStatusNot(request.name(), ProgramStatus.DELETED))
            throw new ApiException(ResponseStatus.PROGRAM_ALREADY_EXISTS);

        update(request, program);

        List<ProgramExpert> experts = programExpertRepository.findAllByProgramId(program.getId());
        return toResponse(programRepository.save(program), experts);
    }

    public ProgramResponse updateStatusById(Long id, ProgramStatus status) {
        if (status.equals(ProgramStatus.DELETED))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        Program program = programRepository.findByIdAndStatusNot(id, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        program.setStatus(status);
        List<ProgramExpert> experts = programExpertRepository.findAllByProgramId(program.getId());

        return toResponse(programRepository.save(program), experts);
    }

    public void deleteById(Long id) {
        Program program = programRepository.findByIdAndStatusNot(id, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isPresent()) {
            program.softDelete(UUID.fromString(currentUserId.get()));
            programRepository.save(program);
        } else {
            log.error("User details are not present in the context for supervisor in ProgramService.delete()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }
    }
}
