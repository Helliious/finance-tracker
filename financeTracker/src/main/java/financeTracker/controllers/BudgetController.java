package financeTracker.controllers;

import financeTracker.models.dto.budget_dto.CreateBudgetRequestDTO;
import financeTracker.models.dto.budget_dto.BudgetWithoutAccountAndOwnerDTO;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.models.pojo.Budget;
import financeTracker.services.BudgetService;
import financeTracker.utils.SessionManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BudgetController extends AbstractController {
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private ModelMapper modelMapper;

    @PutMapping("/budgets")
    public BudgetWithoutAccountAndOwnerDTO addBudget(@RequestBody CreateBudgetRequestDTO dto,
                                                     HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Budget budget = budgetService.addBudgetToAcc(userId, dto);
        return convertToBudgetWithoutAccountAndOwnerDTO(budget);
    }

    @GetMapping("budgets/{budget_id}")
    public BudgetWithoutAccountAndOwnerDTO getById(@PathVariable(name = "budget_id") int budgetId,
                                                   HttpSession session) {
       int userId = sessionManager.getLoggedId(session);
       Budget budget = budgetService.getById(userId, budgetId);
       return new BudgetWithoutAccountAndOwnerDTO(budget);
    }

    @GetMapping("/budgets")
    public List<BudgetWithoutAccountAndOwnerDTO> getAllByUser(HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        List<Budget> budgets = budgetService.getByOwnerId(userId);
        List<BudgetWithoutAccountAndOwnerDTO> resultBudget = new ArrayList<>();
        for (Budget b : budgets) {
            resultBudget.add(new BudgetWithoutAccountAndOwnerDTO(b));
        }
        return resultBudget;
    }

    @GetMapping("/budgets/accounts/{account_id}")
    public List<BudgetWithoutAccountAndOwnerDTO> getAllByAccount(@PathVariable(name = "account_id") int accountId,
                                                                 HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        List<Budget> budgets = budgetService.getByAccountId(userId, accountId);
        List<BudgetWithoutAccountAndOwnerDTO> resultBudget = new ArrayList<>();
        for (Budget b : budgets) {
            resultBudget.add(new BudgetWithoutAccountAndOwnerDTO(b));
        }
        return resultBudget;
    }

    @DeleteMapping("/accounts/{account_id}/budgets/{budget_id}")
    public BudgetWithoutAccountAndOwnerDTO delete(@PathVariable(name = "account_id") int accountId,
                                                  @PathVariable(name = "budget_id") int budgetId,
                                                  HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        return budgetService.delete(budgetId, accountId, userId);
    }

    @PostMapping("/accounts/{account_id}/budgets/{budget_id}")
    public BudgetWithoutAccountAndOwnerDTO edit(@PathVariable(name = "account_id") int accountId,
                                                @PathVariable(name = "budget_id") int budgetId,
                                                @RequestBody CreateBudgetRequestDTO dto,
                                                HttpSession session ) {
        int userId = sessionManager.getLoggedId(session);
        Budget budget =  budgetService.editBudget(budgetId, dto, userId, accountId);
        return new BudgetWithoutAccountAndOwnerDTO(budget);
    }

    @GetMapping("budgets/categories")
    public double getSpendingByCategory(HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        return budgetService.getSpending(userId);
    }

    @PostMapping("/budgets")
    public List<BudgetWithoutAccountAndOwnerDTO> filter(@RequestBody FilterBudgetRequestDTO dto,
                                                        HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        List<Budget> budgets = budgetService.filter(userId, dto);
        List<BudgetWithoutAccountAndOwnerDTO> resultBudget = new ArrayList<>();
        for (Budget b : budgets) {
            resultBudget.add(new BudgetWithoutAccountAndOwnerDTO(b));
        }
        return resultBudget;
    }

    private BudgetWithoutAccountAndOwnerDTO convertToBudgetWithoutAccountAndOwnerDTO(Budget budget) {
        return modelMapper.map(budget, BudgetWithoutAccountAndOwnerDTO.class);
    }
}
