package financeTracker.models.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    private int id;
    private String name;
    private String label;
    private double amount;
    private Timestamp dueTime;
}
