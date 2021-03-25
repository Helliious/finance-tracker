package financeTracker.models.pojo;

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
    private double balance;
    private double accLimit;
    private Timestamp createTime;
    @ManyToOne
    @JoinColumn(name="owner_id")
    private User user;
    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;
}
