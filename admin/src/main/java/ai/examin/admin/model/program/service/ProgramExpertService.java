package ai.examin.admin.model.program.service;

import ai.examin.admin.model.clients.auth.AuthServiceClient;
import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.program.dto.ProgramResponse;
import ai.examin.admin.model.program.entity.Program;
import ai.examin.admin.model.program.entity.ProgramExpert;
import ai.examin.admin.model.program.repository.ProgramExpertRepository;
import ai.examin.admin.model.program.repository.ProgramRepository;
import ai.examin.core.enums.SpecialistProgramStatus;
import ai.examin.core.enums.ProgramStatus;
import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.enums.Role;
import ai.examin.core.exception_handler.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static ai.examin.admin.model.program.mapper.ProgramExpertMapper.toEntity;
import static ai.examin.admin.model.program.mapper.ProgramMapper.toResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramExpertService {
    private final ProgramExpertRepository programExpertRepository;
    private final ProgramRepository programRepository;
    private final AuthServiceClient authServiceClient;


    public ProgramResponse assignExpert(Long programId, UUID expertId) {
        Program program = programRepository.findByIdAndStatusNot(programId, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        UserResponse expert = authServiceClient.getUserById(expertId);
        if (!expert.getRole().equals(Role.EXPERT))
            throw new ApiException(ResponseStatus.ROLE_MISMATCH_EXCEPTION);

        Optional<ProgramExpert> optionalProgramExpert = programExpertRepository.findByProgram_IdAndExpertId(programId, expertId);
        if (optionalProgramExpert.isEmpty()) {
            programExpertRepository.saveAndFlush(toEntity(program, expert));
        } else {
            ProgramExpert programExpert = optionalProgramExpert.get();
            if (programExpert.getStatus().equals(SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_ASSIGNED);

            programExpert.setStatus(SpecialistProgramStatus.ASSIGNED);
            programExpertRepository.saveAndFlush(programExpert);
        }

        return toResponse(program, programExpertRepository.findAllByProgramId(program.getId()));
    }

    public ProgramResponse unassignExpert(Long programId, UUID expertId) {
        Program program = programRepository.findByIdAndStatusNot(programId, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        ProgramExpert programExpert = programExpertRepository
            .findByProgram_IdAndExpertId(program.getId(), expertId)
            .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

        if (programExpert.getStatus().equals(SpecialistProgramStatus.UNASSIGNED))
            throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

        programExpert.setStatus(SpecialistProgramStatus.UNASSIGNED);
        programExpertRepository.saveAndFlush(programExpert);

        return toResponse(program, programExpertRepository.findAllByProgramId(program.getId()));
    }
}
