package financeTracker.services;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.BudgetDAO;
import financeTracker.models.dto.budget_dto.CreateBudgetRequestDTO;
import financeTracker.models.dto.budget_dto.BudgetWithoutAccountAndOwnerDTO;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.models.pojo.*;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.BudgetRepository;
import financeTracker.models.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BudgetDAO budgetDao;

    public Budget getById(int userId, int budgetId) {
        return budgetRepository.findByIdAndOwnerId(budgetId, userId);
    }

    public List<Budget> getByOwnerId(int ownerId) {
        return budgetRepository.findBudgetsByOwnerId(ownerId);
    }

    public List<Budget> getByAccountId(int userId, int accountId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account == null){
            throw new NotFoundException("Account does not exist..");
        }
        return account.getBudgets();
    }

    public BudgetWithoutAccountAndOwnerDTO delete(int budgetId, int accountId, int ownerId) {
        Budget budget = budgetRepository.findByIdAndOwnerIdAndAccountId(budgetId, ownerId, accountId);
        if (budget == null){
            throw new  NotFoundException("Budget doesn't exist");
        }
        BudgetWithoutAccountAndOwnerDTO responseBudget = new BudgetWithoutAccountAndOwnerDTO(budget);
        budgetRepository.deleteById(budgetId);
        return responseBudget;
    }

    public Budget addBudgetToAcc(int userId, CreateBudgetRequestDTO dto) {
        dto.setCreateTime(new Timestamp(System.currentTimeMillis()));
        Account account= accountRepository.findByIdAndOwnerId(dto.getAccountId(), userId);
        Category category = categoryRepository.findByIdAndOwnerId(dto.getCategoryId(), userId);
        if (account == null){
            throw new  NotFoundException("Account doesn't exist");
        }
        if (category == null){
            throw new NotFoundException("Category doesn't exist");
        }
        Budget budget = new Budget(dto);
        budget.setOwner(account.getOwner());
        budget.setAccount(account);
        budget.setCategory(category);
        budgetRepository.save(budget);
        return budget;
    }

    public Budget editBudget(int budgetId, CreateBudgetRequestDTO dto, int userId, int accountId) {
        Budget budget = budgetRepository.findByIdAndOwnerIdAndAccountId(budgetId, userId, accountId);
        Account account = accountRepository.findByIdAndOwnerId(dto.getAccountId(), userId);
        Category category = categoryRepository.findByIdAndOwnerId(dto.getCategoryId(), userId);
        if (budget == null){
            throw new NotFoundException("Budget doesn't exist");
        }
        if (account == null){
            throw new NotFoundException("Account doesn't exist");
        }
        if (category == null){
            throw new NotFoundException("Category doesn't exist");
        }

        if (dto.getName()!=null){
            budget.setName(dto.getName());
        }
        if (dto.getAmount() != null){
            budget.setAmount(dto.getAmount());
        }
        if (dto.getDueTime() != null){
            budget.setDueTime(dto.getDueTime());
        }
        if (dto.getLabel() != null){
            budget.setLabel(dto.getLabel());
        }
        budget.setAccount(account);
        budget.setCategory(category);
        budgetRepository.save(budget);
        return budget;
    }

    public double getSpending(int ownerId, int categoryId) {
        List<Budget> budgets = budgetRepository.findBudgetsByOwnerIdAndCategoryId(ownerId,categoryId);
        if (budgets.isEmpty()) {
            throw new NotFoundException("This user don't have budgets corresponding to this category");
        }
        double totalSpends = 0;
        for (Budget budget:budgets) {
            totalSpends += budget.getAmount();
        }
        return totalSpends;
    }

    public List<Budget> filter(int userId, FilterBudgetRequestDTO dto) {
        return budgetDao.filterBudget(userId, dto);
    }
}
