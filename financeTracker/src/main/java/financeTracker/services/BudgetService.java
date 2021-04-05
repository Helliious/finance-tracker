package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.BudgetDAO;
import financeTracker.models.dto.budget_dto.CreateBudgetRequestDTO;
import financeTracker.models.dto.budget_dto.BudgetWithoutAccountAndOwnerDTO;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.models.dto.budget_dto.UpdateBudgetRequestDTO;
import financeTracker.models.pojo.*;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BudgetDAO budgetDao;

    public Budget getById(int userId, int budgetId, int accountId) {
        Budget budget = budgetRepository.findByIdAndOwnerIdAndAccountId(budgetId, userId, accountId);
        if (budget == null) {
            throw new NotFoundException("Budget not found!");
        }
        return budget;
    }

    public List<Budget> getByOwnerId(int ownerId) {
        return budgetRepository.findBudgetsByOwnerId(ownerId);
    }

    public List<Budget> getByAccountId(int userId, int accountId) {
        return budgetRepository.findAllByOwnerIdAndAccountId(userId, accountId);
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
        if (account == null){
            throw new  NotFoundException("Account doesn't exist");
        }
        if (dto.getCreateTime().compareTo(dto.getDueTime()) >= 0) {
            throw new BadRequestException("Due time must be bigger than create time");
        }
        for (Budget b : account.getBudgets()) {
            if (b.getName().equals(dto.getName())) {
                throw new BadRequestException("Budget name already taken");
            }
        }
        Budget budget = new Budget(dto);
        budget.setOwner(account.getOwner());
        budget.setAccount(account);
        budgetRepository.save(budget);
        return budget;
    }

    public Budget editBudget(int budgetId, UpdateBudgetRequestDTO dto, int userId, int accountId) {
        Budget budget = budgetRepository.findByIdAndOwnerIdAndAccountId(budgetId, userId, accountId);
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (budget == null){
            throw new NotFoundException("Budget doesn't exist");
        }
        if (account == null){
            throw new NotFoundException("Account doesn't exist");
        }
        if (dto.getName() != null) {
            for (Budget b : account.getBudgets()) {
                if (b.getName().equals(dto.getName())) {
                    throw new BadRequestException("Budget name already taken");
                }
            }
            budget.setName(dto.getName());
        }
        if (dto.getAmount() != null) {
            budget.setAmount(dto.getAmount());
        }
        if (dto.getDueTime() != null) {
            if (dto.getDueTime().compareTo(new Timestamp(System.currentTimeMillis())) <= 0) {
                throw new BadRequestException("Invalid due time");
            }
            budget.setDueTime(dto.getDueTime());
        }
        if (dto.getLabel() != null) {
            budget.setLabel(dto.getLabel());
        }
        if (dto.getDescription() != null) {
            budget.setDescription(dto.getDescription());
        }
        budget.setAccount(account);
        budgetRepository.save(budget);
        return budget;
    }

    public List<Budget> filter(int userId, FilterBudgetRequestDTO dto) {
        return budgetDao.filterBudget(userId, dto);
    }
}
