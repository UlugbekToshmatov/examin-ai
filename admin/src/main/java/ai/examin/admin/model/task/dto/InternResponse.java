package ai.examin.admin.model.task.dto;

import ai.examin.admin.model.task.entity.TaskIntern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InternResponse {
    private UUID id;
    private String fullName;
    private String email;


    public InternResponse(TaskIntern intern) {
        this.id = intern.getInternId();
        this.fullName = intern.getInternFullName();
        this.email = intern.getInternEmail();
    }
}
