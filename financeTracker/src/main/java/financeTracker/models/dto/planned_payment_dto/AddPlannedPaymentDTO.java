package financeTracker.models.dto.planned_payment_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class AddPlannedPaymentDTO {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Payment type is mandatory")
    private String paymentType;
    @NotNull(message = "Frequency is mandatory")
    private int frequency;
    @NotBlank(message = "Duration unit is mandatory")
    private String durationUnit;
    @NotNull(message = "Amount is mandatory")
    private double amount;
    private Timestamp createTime;
    @NotNull(message = "Due time is mandatory")
    private Timestamp dueTime;
    private String description;
    @NotNull(message = "Category is mandatory")
    private int categoryId;
}
