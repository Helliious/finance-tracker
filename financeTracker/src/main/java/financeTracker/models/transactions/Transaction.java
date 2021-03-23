package financeTracker.models.transactions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private long id;
    private String type;
    private double amount;
    private Timestamp createTime;
    private long accountId;
    private long categoryId;
    private long ownerId;

}

