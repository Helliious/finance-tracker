package financeTracker.models.dto.transaction_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@Component
public class AddTransactionRequestDTO {
    private String type;
    private double amount;
    private Timestamp createTime;
    private String description;
    private int categoryId;
    private int userId;
}
