package financeTracker.models.dto.transaction_dto;

import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
@Setter
@Getter
@NoArgsConstructor
@Component
public class ResponseTransactionDTO {
    private String type;
    private double amount;
    private Timestamp createTime;
    private String description;
    private Account account;

    public ResponseTransactionDTO(Transaction transaction){
        this.type=transaction.getType();
        this.amount=transaction.getAmount();
        this.createTime=transaction.getCreateTime();
        this.description=transaction.getDescription();
        this.account=transaction.getAccount();
    }
}
