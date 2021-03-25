package financeTracker.models.dto.user_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class RegisterRequestUserDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private Timestamp createTime;
}
