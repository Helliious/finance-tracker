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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Component
public class AccountWithoutOwnerDTO {
    private int id;
    private String name;
    private double balance;
    private double accLimit;
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
                transactions.add(new TransactionWithoutOwnerAndAccountDTO(t));
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
