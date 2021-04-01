package financeTracker.controllers;

import financeTracker.models.dto.budget_dto.CreateBudgetRequestDTO;
import financeTracker.models.dto.budget_dto.BudgetWithoutAccountAndOwnerDTO;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.services.BudgetService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@RestController
public class BudgetController extends AbstractController {
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private SessionManager sessionManager;

    @PutMapping("/budgets")
    public BudgetWithoutAccountAndOwnerDTO addBudget(@RequestBody CreateBudgetRequestDTO dto,
                                                     HttpSession session){
        dto.setCreateTime(new Timestamp(System.currentTimeMillis()));
        int userId = sessionManager.getLoggedId(session);
        return budgetService.addBudgetToAcc(userId, dto);
    }
    
    @GetMapping("budgets/{budget_id}")
    public BudgetWithoutAccountAndOwnerDTO getById(@PathVariable(name="budget_id") int budgetId,
                                                   HttpSession session) {
       int userId = sessionManager.getLoggedId(session);
       return budgetService.getById(userId, budgetId);
    }

    @GetMapping("/budgets/users/")
    public ArrayList<BudgetWithoutAccountAndOwnerDTO> getAllByUser(HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        return budgetService.getByOwnerId(userId);
    }

    @GetMapping("/budgets/accounts/{account_id}")
    public ArrayList<BudgetWithoutAccountAndOwnerDTO> getAllByAccount(@PathVariable("account_id") int accountId,
                                                                      HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        return budgetService.getByAccountId(userId, accountId);
    }

    @DeleteMapping("/budgets/{budget_id}")
    public BudgetWithoutAccountAndOwnerDTO delete(@PathVariable(name="budget_id") int budgetId,
                                                  HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        return budgetService.delete(budgetId, userId);
    }

    @PostMapping("budgets/{budget_id}")
    public BudgetWithoutAccountAndOwnerDTO edit(@PathVariable(name="budget_id") int budgetId,
                                                @RequestBody CreateBudgetRequestDTO dto,
                                                HttpSession session ) {
        int userId = sessionManager.getLoggedId(session);
        return budgetService.editBudget(budgetId,dto, userId);
    }

    @GetMapping("budgets/category/{category_id}")
    public double getSpendingByCategory(@PathVariable(name="category_id") int categoryId,
                                        HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        return budgetService.getSpendings(userId, categoryId);
    }

    @PostMapping("/budgets/filter")
    public List<BudgetWithoutAccountAndOwnerDTO> filter(@RequestBody FilterBudgetRequestDTO dto,
                                                        HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        return budgetService.filter(userId, dto);
    }
}
