package financeTracker.models.dto.user_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
@Component
public class UpdateRequestUserDTO {
    private String firstName;
    private String lastName;
    private String username;
    @Email(message = "Invalid email format")
    private String email;
}
