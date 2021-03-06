package financeTracker.models.dto.budget_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class CreateBudgetRequestDTO {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "Label cannot be empty")
    private String label;
    @NotNull(message = "Amount cannot be empty")
    @PositiveOrZero(message = "Amount must be positive or zero")
    private Double amount;
    private Timestamp createTime;
    @NotNull(message = "Due time cannot be empty")
    private Timestamp dueTime;
    private String description;
    @NotNull(message = "Account id cannot be empty")
    private int accountId;
}
