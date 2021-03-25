package financeTracker.models.dto.user_dto;


import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.transaction_dto.TransactionWithoutOwnerDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Transaction;
import financeTracker.models.pojo.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Component
public class UserWithoutPassDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Timestamp createTime;
    private List<AccountWithoutOwnerDTO> accounts;
    private List<TransactionWithoutOwnerDTO> transactions;

    public UserWithoutPassDTO(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        username = user.getUsername();
        email = user.getEmail();
        createTime = user.getCreateTime();
        accounts = new ArrayList<>();
        for (Account a : user.getAccounts()) {
            accounts.add(new AccountWithoutOwnerDTO(a));
        }
        transactions = new ArrayList<>();
        for (Transaction t : user.getTransactions()) {
            transactions.add(new TransactionWithoutOwnerDTO(t));
        }
    }
}
