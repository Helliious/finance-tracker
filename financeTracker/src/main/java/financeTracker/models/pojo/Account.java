package financeTracker.models.pojo;

import financeTracker.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="accounts")
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private Double balance;
    private Double accLimit;
    private Timestamp createTime;
    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;
    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;
    @OneToMany(mappedBy = "account")
    private List<PlannedPayment> plannedPayments;

    public void reduceBalance(double amount) {
        if (amount > balance) {
            throw new BadRequestException("Not enough money in account!");
        }
        balance -= amount;
    }

    public void increaseBalance(double amount) {
        if (accLimit != null && balance + amount > accLimit) {
            throw new BadRequestException("Limit reached!");
        }
        balance += amount;
    }
}
