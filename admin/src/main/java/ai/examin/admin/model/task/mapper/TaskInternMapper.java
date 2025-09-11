package ai.examin.admin.model.task.mapper;

import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.task.entity.Task;
import ai.examin.admin.model.task.entity.TaskIntern;
import ai.examin.core.enums.SpecialistProgramStatus;

public class TaskInternMapper {

    public static TaskIntern toEntity(Task task, UserResponse intern, Boolean isMentor) {
        TaskIntern taskIntern = new TaskIntern();
        taskIntern.setTask(task);
        taskIntern.setInternId(intern.getId());
        taskIntern.setInternFullName(intern.getFirstName() + " " + intern.getLastName());
        taskIntern.setInternEmail(intern.getEmail());
        if (isMentor) taskIntern.setStatus(SpecialistProgramStatus.UNASSIGNED);

        return taskIntern;
    }

}
