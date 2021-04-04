package financeTracker.models.dto.transaction_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@Component
public class AddTransactionRequestDTO {
    @NotBlank(message = "Type is mandatory")
    private String type;
    @PositiveOrZero(message = "Amount must be positive or zero")
    private double amount;
    private Timestamp createTime;
    private String description;
    @NotNull(message = "Category is mandatory")
    @Positive(message = "Category id must be positive")
    private int categoryId;
}
