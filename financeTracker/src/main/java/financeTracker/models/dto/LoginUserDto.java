package financeTracker.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginUserDto {
    private String username;
    private String password;
}
