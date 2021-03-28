package financeTracker.models.dto.user_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class ChangePassUserDTO {
    private String password;
    private String confirmPassword;
}
