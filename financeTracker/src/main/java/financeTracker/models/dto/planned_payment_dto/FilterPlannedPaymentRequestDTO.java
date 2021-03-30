package financeTracker.models.dto.planned_payment_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class FilterPlannedPaymentRequestDTO {
    private String name;
    private String paymentType;
    private int frequency;
    private String durationUnit;
    private double amountFrom;
    private double amountTo;
    private Timestamp dueTimeFrom;
    private Timestamp dueTimeTo;
}
