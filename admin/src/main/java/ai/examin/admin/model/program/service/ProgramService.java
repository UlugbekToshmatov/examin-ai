package ai.examin.admin.model.program.service;

import ai.examin.admin.model.clients.auth.AuthServiceClient;
import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.program.dto.ProgramRequest;
import ai.examin.admin.model.program.dto.ProgramResponse;
import ai.examin.admin.model.program.dto.ProgramResponseMin;
import ai.examin.admin.model.program.dto.ProgramUpdateRequest;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.repository.ProgramRepository;
import ai.examin.core.enums.ProgramStatus;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Role;
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
    private final UserContextService userContextService;
    private final AuthServiceClient authServiceClient;


    public List<ProgramResponseMin> getAll() {
        return toResponseList(programRepository.findAllByStatusNot(ProgramStatus.DELETED));
    }

    public ProgramResponse getById(Long id) {
        return toResponse(programRepository.findByIdAndStatusNot(id, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND)));
    }

    public ProgramResponse create(ProgramRequest request) {
        if (programRepository.existsByNameAndStatusNot(request.name(), ProgramStatus.DELETED))
            throw new ApiException(ResponseStatus.PROGRAM_ALREADY_EXISTS);

        // Verify if supervisor exists in local DB
        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            log.error("User details are not present in the context for supervisor in ProgramService.create()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        UserResponse supervisor = authServiceClient.getUserById(UUID.fromString(currentUserId.get()));
        if (supervisor == null)
            throw new ApiException(ResponseStatus.USER_NOT_FOUND);

        // Verify if supervisor's role in local DB matches with the one in Keycloak
        if (!supervisor.getRole().equals(Role.SUPERVISOR))
            throw new ApiException(ResponseStatus.ROLE_MISMATCH_EXCEPTION);

        Program program = toEntity(request, supervisor.getId());
        return toResponse(programRepository.save(program));
    }

    public ProgramResponse updateById(Long id, ProgramUpdateRequest request) {
        Program program = programRepository.findByIdAndStatusNot(id, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        if (programRepository.existsByNameAndStatusNot(request.name(), ProgramStatus.DELETED))
            throw new ApiException(ResponseStatus.PROGRAM_ALREADY_EXISTS);

        update(request, program);

        return toResponse(programRepository.save(program));
    }

    public void deleteById(Long id) {
        Program program = programRepository.findByIdAndStatusNot(id, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        Optional<String> currentUserId = userContextService.getCurrentUserId();
        if (currentUserId.isPresent()) {
            program.softDelete(UUID.fromString(currentUserId.get()));
            programRepository.save(program);
        } else {
            log.error("User details are not present in the context in ProgramService.delete()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }
    }
}
