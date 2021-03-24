package financeTracker.controllers;

import financeTracker.models.pojo.Account;
import financeTracker.models.dao.AccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController extends AbstractController {
    @Autowired
    private AccountDAO accountDao;

    @GetMapping("/accounts/{id}")
    public Account getById(@PathVariable int id) throws Exception {
        Account account = accountDao.getById(id);
        return account;
    }
}
