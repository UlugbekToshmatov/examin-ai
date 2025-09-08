package ai.examin.admin.model.course.dto;

import ai.examin.admin.model.course.entity.CourseMentor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorResponse {
    private UUID id;
    private String fullName;
    private String email;


    public MentorResponse(CourseMentor mentor) {
        this.id = mentor.getMentorId();
        this.fullName = mentor.getMentorFullName();
        this.email = mentor.getMentorEmail();
    }
}
