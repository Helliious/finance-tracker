package financeTracker.models.dto.account_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class CreateAccountDTO {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotNull(message = "Balance cannot be empty")
    @PositiveOrZero(message = "Balance must be positive or zero")
    private Double balance;
    @Positive(message = "Account limit must be positive")
    private Double accLimit;
    private Timestamp createTime;
}
