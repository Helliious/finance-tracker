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
public class Account {
    private int id;
    private String name;
    private double balance;
    private double limit;
    private Timestamp createTime;
}
