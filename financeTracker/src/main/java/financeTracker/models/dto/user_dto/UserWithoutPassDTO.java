package financeTracker.models.dto.user_dto;


import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.planned_payment_dto.ResponsePlannedPaymentDTO;
import financeTracker.models.dto.transaction_dto.TransactionWithoutOwnerAndAccountDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.PlannedPayment;
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
    private List<TransactionWithoutOwnerAndAccountDTO> transactions;
    private List<ResponsePlannedPaymentDTO> plannedPayments;

    public UserWithoutPassDTO(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        username = user.getUsername();
        email = user.getEmail();
        createTime = user.getCreateTime();
        accounts = new ArrayList<>();
        if (user.getAccounts() != null) {
            for (Account a : user.getAccounts()) {
                accounts.add(new AccountWithoutOwnerDTO(a));
            }
        }
        transactions = new ArrayList<>();
        if (user.getTransactions() != null) {
            for (Transaction t : user.getTransactions()) {
                transactions.add(new TransactionWithoutOwnerAndAccountDTO(t));
            }
        }
        plannedPayments = new ArrayList<>();
        if (user.getPlannedPayments() != null) {
            for (PlannedPayment p : user.getPlannedPayments()) {
                plannedPayments.add(new ResponsePlannedPaymentDTO(p));
            }
        }
    }
}
