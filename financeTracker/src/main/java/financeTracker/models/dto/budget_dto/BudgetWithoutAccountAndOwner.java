package financeTracker.models.dto.budget_dto;

import financeTracker.models.pojo.Budget;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
@Getter
@Setter
@NoArgsConstructor
@Component
public class BudgetWithoutAccountAndOwner {
    private int id;
    private String name;
    private String label;
    private double amount;
    private Timestamp dueTime;
    private String categoryName;

    public BudgetWithoutAccountAndOwner(Budget budget) {
        this.id = budget.getId();
        this.name = budget.getName();
        this.label = budget.getLabel();
        this.amount = budget.getAmount();
        this.dueTime = budget.getDueTime();
        this.categoryName = budget.getCategory().getName();
    }
}
