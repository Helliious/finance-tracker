package financeTracker.models.dto.account_dto;

import financeTracker.models.dto.budget_dto.BudgetWithoutAccountAndOwnerDTO;
import financeTracker.models.dto.planned_payment_dto.ResponsePlannedPaymentDTO;
import financeTracker.models.dto.transaction_dto.TransactionWithoutOwnerAndAccountDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Budget;
import financeTracker.models.pojo.PlannedPayment;
import financeTracker.models.pojo.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Component
public class AccountWithoutOwnerDTO {
    @NotNull(message = "Account id cannot be empty")
    private int id;
    @NotBlank(message = "Account name cannot be empty")
    private String name;
    @NotNull(message = "Account balance cannot be empty")
    @PositiveOrZero(message = "Account balance must be positive or zero")
    private Double balance;
    @Positive(message = "Account limit must be positive")
    private Double accLimit;
    private Timestamp createTime;
    private List<TransactionWithoutOwnerAndAccountDTO> transactions;
    private List<ResponsePlannedPaymentDTO> plannedPayments;
    private List<BudgetWithoutAccountAndOwnerDTO> budgets;

    public AccountWithoutOwnerDTO(Account account) {
        id = account.getId();
        name = account.getName();
        balance = account.getBalance();
        accLimit = account.getAccLimit();
        createTime = account.getCreateTime();
        transactions = new ArrayList<>();
        if (account.getTransactions() != null) {
            for (Transaction t : account.getTransactions()) {
                if (t.getBudgetsThatHaveTransaction().isEmpty()) {
                    transactions.add(new TransactionWithoutOwnerAndAccountDTO(t));
                }
            }
        }
        plannedPayments = new ArrayList<>();
        if (account.getPlannedPayments() != null) {
            for (PlannedPayment p : account.getPlannedPayments()) {
                plannedPayments.add(new ResponsePlannedPaymentDTO(p));
            }
        }
        budgets = new ArrayList<>();
        if (account.getBudgets() != null) {
            for (Budget b : account.getBudgets()) {
                budgets.add(new BudgetWithoutAccountAndOwnerDTO(b));
            }
        }
    }
}
