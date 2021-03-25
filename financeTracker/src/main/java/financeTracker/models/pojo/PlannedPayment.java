package financeTracker.models.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.sql.Timestamp;
@Getter
@Setter
@AllArgsConstructor
public class PlannedPayment {
    private int id;
    private String name;
    private String paymentType;
    private int frequency;
    private String durationUnit;
    private double amount;
    private Timestamp dueTime;
    private long accountId;
    private long categoryId;
    private long ownerId;

}
