package financeTracker.models.dto.account_dto;

import financeTracker.exceptions.BadRequestException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@NoArgsConstructor
@Component
public class UpdateRequestAccountDTO {
    private String name;
    @PositiveOrZero(message = "Balance must be positive or zero")
    private Double balance;
    @Positive(message = "Account limit must be positive")
    private Double accLimit;

    public void validate() {
        if (balance < 0) {
            throw new BadRequestException("Invalid balance!");
        }
        if (accLimit <= 0) {
            throw new BadRequestException("Invalid account limit!");
        }
    }
}
