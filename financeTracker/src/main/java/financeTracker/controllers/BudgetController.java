package financeTracker.controllers;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.budget_dto.CreateBudgetRequestDTO;
import financeTracker.models.dto.budget_dto.BudgetWithoutAccountAndOwnerDTO;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.repository.AccountRepository;
import financeTracker.services.BudgetService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Optional;

@RestController
public class BudgetController extends AbstractController {
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SessionManager sessionManager;

    @PutMapping("/budgets/")
    public BudgetWithoutAccountAndOwnerDTO addBudget(@RequestBody CreateBudgetRequestDTO dto,
                                                     HttpSession session){
        //dto.setCreateTime(new Timestamp(System.currentTimeMillis()));
       int userId= sessionManager.validateSession(session);
        return budgetService.addBudgetToAcc(userId,dto);
    }

    @GetMapping("budgets/{budget_id}")
    public BudgetWithoutAccountAndOwnerDTO getById(@PathVariable(name="budget_id") int budgetId,
                                                   HttpSession session) {
       int userId= sessionManager.validateSession(session);
        return budgetService.getById(budgetId);
    }

    @GetMapping("/budgets/users/")
    public ArrayList<BudgetWithoutAccountAndOwnerDTO> getAllByUser(HttpSession session){
        int userId= sessionManager.validateSession(session);
        return budgetService.getByOwnerId(userId);
    }

    @GetMapping("/budgets/accounts/{account_id}")
    public ArrayList<BudgetWithoutAccountAndOwnerDTO> getAllByAccount(@PathVariable("account_id") int accountId,
                                                                      HttpSession session){
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if (optionalAccount.isEmpty()){
            throw new NotFoundException("Account does not exist..");
        }
        sessionManager.validateSession(session);
        return budgetService.getByAccountId(accountId);
    }

    @DeleteMapping("/delete/{budget_id}")
    public BudgetWithoutAccountAndOwnerDTO delete(@PathVariable(name="budget_id") int budgetId,
                                                  HttpSession session){
        int userId=sessionManager.validateSession(session);
        return budgetService.delete(budgetId,userId);
    }

    @PostMapping("budgets/{budget_id}")
    public BudgetWithoutAccountAndOwnerDTO edit(
                                                @PathVariable(name="budget_id") int budgetId,
                                                @RequestBody CreateBudgetRequestDTO dto,
                                                HttpSession session ) {
        int userId=sessionManager.validateSession(session);
        return budgetService.editBudget(budgetId,dto,userId);
    }

    @GetMapping("budgets/category/{category_id}")
    public double getSpendingByCategory(@PathVariable(name="category_id") int categoryId,
                                        HttpSession session){
        sessionManager.validateSession(session);
        return budgetService.getSpendings(categoryId);
    }

    @PostMapping("/budgets/filter")
    public ArrayList<BudgetWithoutAccountAndOwnerDTO> filter(@PathVariable(name="user_id") int userId,
                                                             @RequestBody FilterBudgetRequestDTO dto,
                                                             HttpSession session){
        sessionManager.validateSession(session);
        return budgetService.filter(userId,dto);
    }
}
