package financeTracker.controllers;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.dao.AccountDAO;
import financeTracker.models.pojo.User;
import financeTracker.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

@RestController
public class AccountController extends AbstractController {
    @Autowired
    private AccountDAO accountDao;
    @Autowired
    private AccountService accountService;

    @GetMapping("/users/{user_id}/accounts/{account_id}")
    public AccountWithoutOwnerDTO getById(@PathVariable(name = "user_id") int userId,
                                          @PathVariable(name = "account_id") int accountId,
                                          HttpSession session) {
        if (session.getAttribute("LoggedUser") == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            int loggedId = (int) session.getAttribute("LoggedUser");
            if (loggedId != userId) {
                throw new BadRequestException("Cannot show accounts for other users!");
            }
        }
        return accountService.getById(accountId);
    }

    @PutMapping("/users/{user_id}/create_account")
    public UserWithoutPassDTO create(@PathVariable(name = "user_id") int id,
                                     @RequestBody Account account,
                                     HttpSession session) {
        if (session.getAttribute("LoggedUser") == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            int loggedId = (int) session.getAttribute("LoggedUser");
            if (loggedId != id) {
                throw new BadRequestException("Cannot create accounts for other users!");
            }
        }
        account.setCreateTime(new Timestamp(System.currentTimeMillis()));
        UserWithoutPassDTO userWithoutPassDTO = accountService.createAcc(account, id);
        return userWithoutPassDTO;
    }
}
