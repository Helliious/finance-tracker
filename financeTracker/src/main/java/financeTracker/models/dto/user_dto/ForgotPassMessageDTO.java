package financeTracker.models.dto.user_dto;

import financeTracker.models.pojo.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class ForgotPassMessageDTO {
    private String username;
    private String firstName;
    private String lastName;

    public ForgotPassMessageDTO(User user) {
        username = user.getUsername();
        firstName = user.getFirstName();
        lastName = user.getLastName();
    }
}
