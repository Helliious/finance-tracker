package financeTracker.models.dto.budget_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component

public class CreateBudgetRequestDTO {
    private String name;
    private String label;
    private double amount;
    private Timestamp dueTime;
    private int categoryId;
    private int accountId;
}
