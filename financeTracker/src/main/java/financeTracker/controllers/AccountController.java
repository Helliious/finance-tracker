package financeTracker.controllers;

import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.account_dto.FilterAccountRequestDTO;
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
    @Autowired
    private SessionManager sessionManager;

    @GetMapping("/accounts/{account_id}")
    public AccountWithoutOwnerDTO getById(@PathVariable(name = "account_id") int accountId,
                                          HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return accountService.getById(accountId, userId);
    }

    @GetMapping("/accounts")
    public List<AccountWithoutOwnerDTO> getAll(HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return accountService.getAll(userId);
    }

    @PutMapping("/accounts")
    public UserWithoutPassDTO create(@RequestBody Account account,
                                     HttpSession session) {
        int userId = sessionManager.validateSession(session);
        account.setCreateTime(new Timestamp(System.currentTimeMillis()));
        UserWithoutPassDTO userWithoutPassDTO = accountService.createAcc(account, userId);
        return userWithoutPassDTO;
    }

    @DeleteMapping("/accounts/{account_id}")
    public AccountWithoutOwnerDTO delete(@PathVariable(name = "account_id") int accountId,
                                         HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return accountService.deleteAccount(accountId, userId);
    }

    @PostMapping("/accounts/{account_id}")
    public UserWithoutPassDTO edit(@PathVariable(name = "account_id") int accountId,
                                   @RequestBody UpdateRequestAccountDTO updateRequestAccountDTO,
                                   HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return accountService.editAccount(updateRequestAccountDTO, userId, accountId);
    }

    @PostMapping("/accounts/filter")
    public List<AccountWithoutOwnerDTO> filter(@RequestBody FilterAccountRequestDTO accountRequestDTO,
                                               HttpSession session){
        int userId = sessionManager.validateSession(session);
        return accountService.filter(userId, accountRequestDTO);
    }
}
