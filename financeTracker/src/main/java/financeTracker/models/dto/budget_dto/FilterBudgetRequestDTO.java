package financeTracker.models.dto.budget_dto;

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
public class FilterBudgetRequestDTO {
    private String name;
    @PositiveOrZero(message = "Amount from must be positive or zero")
    private Double amountFrom;
    @Positive(message = "Amount to must be positive or zero")
    private Double amountTo;
    private Timestamp dateFrom;
    private Timestamp dateTo;
}
