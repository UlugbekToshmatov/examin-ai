package ai.examin.admin.model.task.dto;

import ai.examin.admin.model.task.entity.Task;
import ai.examin.core.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseMin {
    private Long id;
    private String title;
    private TaskStatus status;


    public TaskResponseMin(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.status = task.getStatus();
    }
}
