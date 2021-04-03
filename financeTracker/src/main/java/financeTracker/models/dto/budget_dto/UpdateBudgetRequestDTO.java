package financeTracker.models.dto.budget_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class UpdateBudgetRequestDTO {
    private String name;
    private String label;
    @PositiveOrZero(message = "Amount must be positive or zero")
    private Double amount;
    private Timestamp dueTime;
    private String description;
}
