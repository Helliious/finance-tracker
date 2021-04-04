package financeTracker.models.dto.transaction_dto;

import financeTracker.models.pojo.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class RasponseTransactionDTO {
    private int id;
    private String type;
    private double amount;
    private Timestamp createTime;
    private String categoryName;
    private String description;

    public RasponseTransactionDTO(Transaction transaction) {
        id = transaction.getId();
        type = transaction.getType();
        amount = transaction.getAmount();
        createTime = transaction.getCreateTime();
        categoryName = transaction.getCategory().getName();
        description=transaction.getDescription();
    }

}
