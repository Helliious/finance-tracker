package financeTracker.models.dto.user_dto;

import financeTracker.utils.ValidPassword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class RegisterRequestUserDTO {
    @NotBlank(message = "First Name is mandatory")
    private String firstName;
    @NotBlank(message = "Last Name is mandatory")
    private String lastName;
    @NotBlank(message = "Username is mandatory")
    private String username;
    @ValidPassword
    @NotBlank(message = "Password is mandatory")
    private String password;
    @ValidPassword
    @NotBlank(message = "Confirm password is mandatory")
    private String confirmPassword;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;
    private Timestamp createTime;
}
