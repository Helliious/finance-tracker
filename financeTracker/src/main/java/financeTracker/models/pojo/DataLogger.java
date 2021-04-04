package financeTracker.models.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "data_logs")
@NoArgsConstructor
@AllArgsConstructor
public class DataLogger {
    private long id;
    private String username;
    private double overallBalance;
    private int accountsCount;
    private int transactionsCount;
    private int plannedPaymentsCount;
    private int budgetsCount;

    @Id
    public Long getId() {
        return id;
    }
}
