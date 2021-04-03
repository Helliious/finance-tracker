package financeTracker.models.dto.account_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class FilterAccountRequestDTO {
    private String name;
    @PositiveOrZero(message = "Balance from must be positive or zero")
    private Double balanceFrom;
    @Positive(message = "Balance to must be positive")
    private Double balanceTo;
    @Positive(message = "Account limit from must be positive")
    private Double accLimitFrom;
    @Positive(message = "Account limit to must be positive")
    private Double accLimitTo;
    private Timestamp createTimeFrom;
    private Timestamp createTimeTo;
}
