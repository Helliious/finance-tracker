package financeTracker.models.dto.user_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class UpdateRequestUserDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
}
