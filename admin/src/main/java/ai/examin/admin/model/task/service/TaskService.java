package ai.examin.admin.model.task.service;

import ai.examin.admin.model.program.repository.ProgramRepository;
import ai.examin.admin.model.task.repository.TaskRepository;
import ai.examin.core.utils.UserContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProgramRepository programRepository;
    private final UserContextService userContextService;



}
