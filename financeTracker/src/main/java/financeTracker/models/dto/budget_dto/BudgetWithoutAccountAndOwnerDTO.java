package financeTracker.models.dto.budget_dto;

import financeTracker.models.dto.transaction_dto.TransactionWithoutOwnerAndAccountDTO;
import financeTracker.models.pojo.Budget;
import financeTracker.models.pojo.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Component
public class BudgetWithoutAccountAndOwnerDTO {
    private int id;
    private String name;
    private String label;
    private double amount;
    private Timestamp dueTime;
    private String description;
    private List<TransactionWithoutOwnerAndAccountDTO> transactionList;

    public BudgetWithoutAccountAndOwnerDTO(Budget budget) {
        this.id = budget.getId();
        this.name = budget.getName();
        this.label = budget.getLabel();
        this.amount = budget.getAmount();
        this.dueTime = budget.getDueTime();
        this.description = budget.getDescription();
        this.transactionList = new ArrayList<>();
        if (budget.getBudgetTransactions() != null){
            for (Transaction transaction : budget.getBudgetTransactions()) {
                transactionList.add(new TransactionWithoutOwnerAndAccountDTO(transaction));
            }
        }
    }
}
