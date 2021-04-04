package financeTracker.models.dto.planned_payment_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class FilterPlannedPaymentRequestDTO {
    private String name;
    private String paymentType;
    @Positive(message = "Frequency must be positive")
    private Integer frequency;
    private String durationUnit;
    @PositiveOrZero(message = "Amount from must be positive or zero")
    private Double amountFrom;
    @Positive(message = "Amount to must be positive")
    private Double amountTo;
    private Timestamp createTimeFrom;
    private Timestamp createTimeTo;
    private Timestamp dueTimeFrom;
    private Timestamp dueTimeTo;
}
