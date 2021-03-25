package financeTracker.models.dto.account_dto;

import financeTracker.models.pojo.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class AccountWithoutOwnerDTO {
    private int id;
    private String name;
    private double balance;
    private double accLimit;
    private Timestamp createTime;

    public AccountWithoutOwnerDTO(Account account) {
        id = account.getId();
        name = account.getName();
        balance = account.getBalance();
        accLimit = account.getAccLimit();
        createTime = account.getCreateTime();
    }
}
