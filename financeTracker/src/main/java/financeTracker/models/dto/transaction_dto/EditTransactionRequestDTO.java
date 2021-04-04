package financeTracker.models.dto.transaction_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Setter
@Getter
@NoArgsConstructor
@Component
public class EditTransactionRequestDTO {
    private String type;
    @PositiveOrZero(message = "Amount must be positive or zero")
    private Double amount;
    private String description;
    @Positive(message = "Category id must be positive")
    private Integer categoryId;
    @Positive(message = "Account id must be positive")
    private Integer accountId;
}
