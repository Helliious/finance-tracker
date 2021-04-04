package financeTracker.models.dto.planned_payment_dto;

import financeTracker.models.dto.category_dto.ResponseCategoryDTO;
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
public class EditPlannedPaymentDTO {
    private String name;
    @Positive(message = "Frequency must be positive")
    private Integer frequency;
    private String durationUnit;
    @PositiveOrZero(message = "Amount must be positive or zero")
    private Double amount;
    private Timestamp dueTime;
    private String description;
    private ResponseCategoryDTO category;
}
