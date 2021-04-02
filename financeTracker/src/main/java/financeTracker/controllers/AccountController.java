package financeTracker.controllers;

import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.account_dto.CreateAccountDTO;
import financeTracker.models.dto.account_dto.FilterAccountRequestDTO;
import financeTracker.models.dto.account_dto.UpdateRequestAccountDTO;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.User;
import financeTracker.services.AccountService;
import financeTracker.utils.SessionManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AccountController extends AbstractController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/accounts/{account_id}")
    public AccountWithoutOwnerDTO getById(@PathVariable(name = "account_id") int accountId,
                                          HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Account account = accountService.getById(accountId, userId);
        return new AccountWithoutOwnerDTO(account);
    }

    @GetMapping("/accounts")
    public List<AccountWithoutOwnerDTO> getAll(HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        List<Account> accounts = accountService.getAll(userId);
        List<AccountWithoutOwnerDTO> resultAcc = new ArrayList<>();
        for (Account a : accounts) {
            resultAcc.add(new AccountWithoutOwnerDTO(a));
        }
        return resultAcc;
    }

    @PutMapping("/accounts")
    public UserWithoutPassDTO create(@RequestBody CreateAccountDTO createAccountDTO,
                                     HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        User user = accountService.createAcc(createAccountDTO, userId);
        return new UserWithoutPassDTO(user);
    }

    @DeleteMapping("/accounts/{account_id}")
    public AccountWithoutOwnerDTO delete(@PathVariable(name = "account_id") int accountId,
                                         HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Account account = accountService.deleteAccount(accountId, userId);
        return new AccountWithoutOwnerDTO(account);
    }

    @PostMapping("/accounts/{account_id}")
    public AccountWithoutOwnerDTO edit(@PathVariable(name = "account_id") int accountId,
                                   @RequestBody UpdateRequestAccountDTO updateRequestAccountDTO,
                                   HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Account account = accountService.editAccount(updateRequestAccountDTO, userId, accountId);
        return new AccountWithoutOwnerDTO(account);
    }

    @PostMapping("/accounts/filter")
    public List<AccountWithoutOwnerDTO> filter(@RequestBody FilterAccountRequestDTO accountRequestDTO,
                                               HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        List<Account> accounts = accountService.filter(userId, accountRequestDTO);
        List<AccountWithoutOwnerDTO> resultAcc = new ArrayList<>();
        for (Account a : accounts) {
            resultAcc.add(new AccountWithoutOwnerDTO(a));
        }
        return resultAcc;
    }
}
