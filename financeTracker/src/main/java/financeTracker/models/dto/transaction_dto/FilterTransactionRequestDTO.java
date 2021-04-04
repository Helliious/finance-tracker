package financeTracker.models.dto.transaction_dto;

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
public class FilterTransactionRequestDTO{
    private String name;
    @Positive(message = "Category id must be positive")
    private Integer categoryId;
    @PositiveOrZero(message = "Amount from must be positive or zero")
    private Integer amountFrom;
    @Positive(message = "Amount to must be positive")
    private Integer amountTo;
    private String type;
    private Timestamp dateFrom;
    private Timestamp dateTo;
}
