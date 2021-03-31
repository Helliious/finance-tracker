package financeTracker.controllers;

import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
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
        int userId = sessionManager.validateSession(session);
        Account account = accountService.getById(accountId, userId);
        return convertToAccWithoutOwnerDTO(account);
    }

    @GetMapping("/accounts")
    public List<AccountWithoutOwnerDTO> getAll(HttpSession session) {
        int userId = sessionManager.validateSession(session);
        List<Account> accounts = accountService.getAll(userId);
        return accounts.stream()
                .map(this::convertToAccWithoutOwnerDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/accounts")
    public UserWithoutPassDTO create(@RequestBody Account account,
                                     HttpSession session) {
        int userId = sessionManager.validateSession(session);
        User user = accountService.createAcc(account, userId);
        return convertToUserWithoutPassDTO(user);
    }

    @DeleteMapping("/accounts/{account_id}")
    public AccountWithoutOwnerDTO delete(@PathVariable(name = "account_id") int accountId,
                                         HttpSession session) {
        int userId = sessionManager.validateSession(session);
        Account account = accountService.deleteAccount(accountId, userId);
        return convertToAccWithoutOwnerDTO(account);
    }

    @PostMapping("/accounts/{account_id}")
    public AccountWithoutOwnerDTO edit(@PathVariable(name = "account_id") int accountId,
                                   @RequestBody UpdateRequestAccountDTO updateRequestAccountDTO,
                                   HttpSession session) {
        int userId = sessionManager.validateSession(session);
        Account account = accountService.editAccount(updateRequestAccountDTO, userId, accountId);
        return convertToAccWithoutOwnerDTO(account);
    }

    @PostMapping("/accounts/filter")
    public List<AccountWithoutOwnerDTO> filter(@RequestBody FilterAccountRequestDTO accountRequestDTO,
                                               HttpSession session){
        int userId = sessionManager.validateSession(session);
        List<Account> accounts = accountService.filter(userId, accountRequestDTO);
        return accounts.stream()
                .map(this::convertToAccWithoutOwnerDTO)
                .collect(Collectors.toList());
    }

    private AccountWithoutOwnerDTO convertToAccWithoutOwnerDTO(Account account) {
        return modelMapper.map(account, AccountWithoutOwnerDTO.class);
    }

    private UserWithoutPassDTO convertToUserWithoutPassDTO(User user) {
        return modelMapper.map(user, UserWithoutPassDTO.class);
    }
}
