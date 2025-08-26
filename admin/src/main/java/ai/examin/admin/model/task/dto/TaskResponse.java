package ai.examin.admin.model.task.dto;

import ai.examin.admin.model.program.dto.ProgramResponseMin;
import ai.examin.admin.model.task.entity.Task;
import ai.examin.core.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private ProgramResponseMin program;
    private Long mentorId;
    private String title;
    private String definition;
    private TaskStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;


    public TaskResponse(Task task) {
        this.id = task.getId();
        this.program = new ProgramResponseMin(task.getProgram());
        this.mentorId = task.getMentorId();
        this.title = task.getTitle();
        this.definition = task.getDefinition();
        this.status = task.getStatus();
        this.createdAt = task.getCreatedAt().toString();
    }
}
