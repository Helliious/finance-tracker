package financeTracker.models.dto.transaction_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class FilterTransactionRequestDTO{
    private String name;
    private Integer categoryId;
    private Integer amountFrom;
    private Integer amountTo;
    private String type;
    private Timestamp dateFrom;
    private Timestamp dateTo;
}
