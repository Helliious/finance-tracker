package financeTracker.controllers;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.budget_dto.CreateBudgetRequestDTO;
import financeTracker.models.dto.budget_dto.BudgetWithoutAccountAndOwner;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.models.dto.transaction_dto.TransactionWithoutOwnerDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.dao.BudgetDAO;
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
    private  BudgetDAO budgetDao;
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/users/{user_id}/add/budget")
    public BudgetWithoutAccountAndOwner addBudget(@RequestBody CreateBudgetRequestDTO dto
            , @PathVariable("user_id") int userId
            , HttpSession session){
        //dto.setCreateTime(new Timestamp(System.currentTimeMillis()));
        SessionManager.validateSession(session,"Cannot add to other user !!",userId);
        return budgetService.addBudgetToAcc(userId,dto);
    }
    @GetMapping("users/{user_id}/budgets/{budget_id}")
    public BudgetWithoutAccountAndOwner getById(@PathVariable(name = "user_id") int ownerId
            , @PathVariable(name="budget_id") int budgetId, HttpSession session) {
        SessionManager.validateSession(session,"Cannot get other user budget!!",ownerId);
        return budgetService.getById(budgetId);
    }
    @GetMapping("/users/{owner_id}/budgets")
    public ArrayList<BudgetWithoutAccountAndOwner> getAllByUser(@PathVariable("owner_id") int ownerId
            , HttpSession session){
        SessionManager.validateSession(session,"Cannot see other users budgets!!",ownerId);
        return budgetService.getByOwnerId(ownerId);
    }
    @GetMapping("/accounts/{account_id}/budgets")
    public ArrayList<BudgetWithoutAccountAndOwner> getAllByAccount(@PathVariable("account_id") int accountId
            ,HttpSession session){
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if (optionalAccount.isEmpty()){
            throw new NotFoundException("Account does not exist..");
        }
        Account account=optionalAccount.get();
        int ownerId=account.getOwner().getId();
        SessionManager.validateSession(session,"Cannot see other users account budgets!!",ownerId);
        return budgetService.getByAccountId(accountId);
    }
    @DeleteMapping("/users/{user_id}/budgets/delete/{budget_id}")
    public BudgetWithoutAccountAndOwner delete(@PathVariable(name="user_id") int userId
            ,@PathVariable(name="budget_id") int budgetId
            ,HttpSession session){
        SessionManager.validateSession(session,"Cannot delete other users budget!!",userId);
        return budgetService.delete(budgetId,userId);
    }
    @PutMapping("users/{user_id}/budgets/{budget_id}/edit")
    public BudgetWithoutAccountAndOwner edit(@PathVariable(name = "user_id") int userId
            ,@PathVariable(name="budget_id") int budgetId
            , @RequestBody CreateBudgetRequestDTO dto,
                                           HttpSession session ) {
        SessionManager.validateSession(session,"You can't modify other users budgets",userId);
        return budgetService.editBudget(budgetId,dto,userId);
    }
    @GetMapping("users/{user_id}/category/{category_id}/spendings")
    public double getSpendingByCategory(@PathVariable(name="user_id") int userId
            ,@PathVariable(name="category_id") int categoryId
            ,HttpSession session){
        SessionManager.validateSession(session,"You can't get spendings of other user",userId);
        return budgetService.getSpendings(categoryId);
    }
    @PostMapping("users/{user_id}/budgets/filter")
    public ArrayList<TransactionWithoutOwnerDTO> filter(@PathVariable(name="user_id") int userId
            , @RequestBody FilterBudgetRequestDTO dto){
        //todo make filter..
        return new ArrayList<>();
    }
}
