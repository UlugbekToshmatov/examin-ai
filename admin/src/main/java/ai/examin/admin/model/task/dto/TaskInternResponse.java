package ai.examin.admin.model.task.dto;

import ai.examin.admin.model.clients.auth.dto.UserResponse;
import ai.examin.admin.model.program.dto.ProgramResponseMin;
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
public class TaskInternResponse {
    private Long id;
    private ProgramResponseMin program;
    private UserResponse mentor;
    private String title;
    private String definition;
    private TaskStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
}
