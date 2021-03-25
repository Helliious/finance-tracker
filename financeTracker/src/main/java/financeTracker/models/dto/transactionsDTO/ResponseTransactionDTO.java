package financeTracker.models.dto.transactionsDTO;

import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Transaction;

import java.sql.Timestamp;

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
