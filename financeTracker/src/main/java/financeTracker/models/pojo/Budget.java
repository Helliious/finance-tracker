package financeTracker.models.pojo;

import financeTracker.models.dto.budget_dto.CreateBudgetRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String label;
    private double amount;
    private Timestamp createTime;
    private Timestamp dueTime;
    private String description;
    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;
    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;
    @ManyToMany
    @JoinTable(
            name = "budgets_have_transactions",
            joinColumns = {@JoinColumn(name = "budget_id")},
            inverseJoinColumns = {@JoinColumn(name = "transaction_id")}
    )
    private List<Transaction> budgetTransactions;

    public Budget(CreateBudgetRequestDTO dto){
        name = dto.getName();
        label = dto.getLabel();
        amount = dto.getAmount();
        createTime = dto.getCreateTime();
        dueTime = dto.getDueTime();
        description = dto.getDescription();
        budgetTransactions = new ArrayList<>();
    }
}
