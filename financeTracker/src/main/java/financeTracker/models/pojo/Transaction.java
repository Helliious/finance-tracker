package financeTracker.models.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import financeTracker.models.dto.transaction_dto.AddTransactionRequestDTO;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="transactions")
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String type;
    private double amount;
    private Timestamp createTime;
    private String description;
    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;
    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;
    @ManyToMany(mappedBy = "transactions")
    //One transaction can be part of more than one budgets..
    @JsonBackReference
    private List<Transaction> budgetsThatHaveTransaction;
    public Transaction(AddTransactionRequestDTO dto){
         type=dto.getType();
         amount=dto.getAmount();
         createTime=dto.getCreateTime();
         description=dto.getDescription();
    }
}

