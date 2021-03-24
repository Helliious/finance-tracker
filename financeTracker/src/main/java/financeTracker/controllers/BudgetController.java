package financeTracker.controllers;

import financeTracker.models.pojo.Budget;
import financeTracker.models.dao.BudgetDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BudgetController extends AbstractController {
    @Autowired
    private BudgetDAO budgetDao;

    @GetMapping("/budgets/{id}")
    public Budget getById(@PathVariable int id) throws Exception {
        Budget budget = budgetDao.getById(id);
        return budget;
    }
}
