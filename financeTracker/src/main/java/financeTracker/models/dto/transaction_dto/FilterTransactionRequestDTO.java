package financeTracker.models.dto.transaction_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class FilterTransactionRequestDTO{
    private String name;
    private int categoryId;
    private int amountFrom;
    private int amountTo;
    private String type;
}
