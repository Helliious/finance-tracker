package financeTracker.models.pojo;

import financeTracker.models.dto.budget_dto.CreateBudgetRequestDTO;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.models.dto.transaction_dto.FilterTransactionRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

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
    private Timestamp dueTime;
    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;
    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;
    public Budget(CreateBudgetRequestDTO dto){
        name=dto.getName();
        label=dto.getLabel();
        amount=dto.getAmount();
        dueTime=dto.getDueTime();
    }


}
