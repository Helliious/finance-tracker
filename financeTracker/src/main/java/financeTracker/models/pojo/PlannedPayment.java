package financeTracker.models.pojo;

import financeTracker.models.dto.planned_payment_dto.AddPlannedPaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;


@Getter
@Setter
@Entity
@Table(name = "planned_payments")
@NoArgsConstructor
@AllArgsConstructor
public class PlannedPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String paymentType;
    private int frequency;
    private String durationUnit;
    private double amount;
    private Timestamp createTime;
    private Timestamp dueTime;
    private String description;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public PlannedPayment(AddPlannedPaymentDTO plannedPaymentDTO) {
        name = plannedPaymentDTO.getName();
        paymentType = plannedPaymentDTO.getPaymentType();
        frequency = plannedPaymentDTO.getFrequency();
        durationUnit = plannedPaymentDTO.getDurationUnit();
        amount = plannedPaymentDTO.getAmount();
        createTime = plannedPaymentDTO.getCreateTime();
        dueTime = plannedPaymentDTO.getDueTime();
        description = plannedPaymentDTO.getDescription();
    }
}
