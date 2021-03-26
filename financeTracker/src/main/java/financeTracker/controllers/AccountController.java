package financeTracker.controllers;

import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.account_dto.UpdateRequestAccountDTO;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.Account;
import financeTracker.services.AccountService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

@RestController
public class AccountController extends AbstractController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/users/{user_id}/accounts/{account_id}")
    public AccountWithoutOwnerDTO getById(@PathVariable(name = "user_id") int userId,
                                          @PathVariable(name = "account_id") int accountId,
                                          HttpSession session) {
        String message = "Cannot show accounts of other users!";
        SessionManager.validateSession(session, message, userId);
        return accountService.getById(accountId);
    }

    @GetMapping("/users/{user_id}/accounts")
    public List<AccountWithoutOwnerDTO> getAll(@PathVariable(name = "user_id") int userId, HttpSession session) {
        String message = "Cannot show accounts of other users!";
        SessionManager.validateSession(session, message, userId);
        return accountService.getAll(userId);
    }

    @PutMapping("/users/{user_id}/create_account")
    public UserWithoutPassDTO create(@PathVariable(name = "user_id") int id,
                                     @RequestBody Account account,
                                     HttpSession session) {
        String message = "Cannot create accounts for other users!";
        SessionManager.validateSession(session, message, id);
        account.setCreateTime(new Timestamp(System.currentTimeMillis()));
        UserWithoutPassDTO userWithoutPassDTO = accountService.createAcc(account, id);
        return userWithoutPassDTO;
    }

    @DeleteMapping("/users/{user_id}/accounts/{account_id}/delete_account")
    public AccountWithoutOwnerDTO delete(@PathVariable(name = "user_id") int userId,
                                         @PathVariable(name = "account_id") int accountId,
                                         HttpSession session) {
        String message = "Cannot delete accounts for other users!";
        SessionManager.validateSession(session, message, userId);
        return accountService.deleteAccount(accountId);
    }

    @PutMapping("/users/{user_id}/accounts/{account_id}/edit_account")
    public UserWithoutPassDTO edit(@PathVariable(name = "user_id") int userId,
                                   @PathVariable(name = "account_id") int accountId,
                                   @RequestBody UpdateRequestAccountDTO updateRequestAccountDTO,
                                   HttpSession session) {
        String message = "Cannot modify accounts for other users!";
        SessionManager.validateSession(session, message, userId);
        return accountService.editAccount(updateRequestAccountDTO, userId, accountId);
    }
}
