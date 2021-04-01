package financeTracker.models.dto.transaction_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class EditTransactionRequestDTO {
    private String type;
    private Double amount;
    private String description;
    private Integer categoryId;
    private Integer accountId;
}
