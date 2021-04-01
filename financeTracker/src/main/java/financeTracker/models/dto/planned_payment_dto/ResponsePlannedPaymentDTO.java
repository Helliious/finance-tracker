package financeTracker.models.dto.planned_payment_dto;

import financeTracker.models.dto.category_dto.ResponseCategoryDTO;
import financeTracker.models.pojo.PlannedPayment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class ResponsePlannedPaymentDTO {
    private int id;
    private String name;
    private String paymentType;
    private Integer frequency;
    private String durationUnit;
    private Double amount;
    private Timestamp dueTime;
    private ResponseCategoryDTO category;

    public ResponsePlannedPaymentDTO(PlannedPayment plannedPayment) {
        id = plannedPayment.getId();
        name = plannedPayment.getName();
        paymentType = plannedPayment.getPaymentType();
        frequency = plannedPayment.getFrequency();
        durationUnit = plannedPayment.getDurationUnit();
        amount = plannedPayment.getAmount();
        dueTime = plannedPayment.getDueTime();
        category = new ResponseCategoryDTO(plannedPayment.getCategory());
    }
}
