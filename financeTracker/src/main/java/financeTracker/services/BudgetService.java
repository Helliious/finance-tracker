package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.BudgetDAO;
import financeTracker.models.dto.budget_dto.CreateBudgetRequestDTO;
import financeTracker.models.dto.budget_dto.BudgetWithoutAccountAndOwnerDTO;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.models.pojo.*;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.BudgetRepository;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BudgetDAO budgetDao;

    public BudgetWithoutAccountAndOwnerDTO getById(int id) {
        Optional<Budget> optionalBudget=budgetRepository.findById(id);
        if (optionalBudget.isEmpty()){
            throw new NotFoundException("Budget not found");
        }
        else{
            return new BudgetWithoutAccountAndOwnerDTO(optionalBudget.get());
        }
    }

    public ArrayList<BudgetWithoutAccountAndOwnerDTO> getByOwnerId(int ownerId) {
        ArrayList<Budget> budgets=budgetRepository.findBudgetsByOwnerId(ownerId);
        if (budgets.isEmpty()){
            throw new NotFoundException("User don't have budgets");
        }
        ArrayList<BudgetWithoutAccountAndOwnerDTO> responseBudgets =new ArrayList<>();
        for (Budget budget:budgets){
            BudgetWithoutAccountAndOwnerDTO responseBudget=new BudgetWithoutAccountAndOwnerDTO(budget);
            responseBudgets.add(responseBudget);
        }
        return responseBudgets;
    }

    public ArrayList<BudgetWithoutAccountAndOwnerDTO> getByAccountId(int accountId) {
        ArrayList<Budget> budgets=budgetRepository.findBudgetsByAccountId(accountId);
        if (budgets.isEmpty()){
            throw new NotFoundException("This account dont'have budgets");
        }
        ArrayList<BudgetWithoutAccountAndOwnerDTO> responseBudgets=new ArrayList<>();
        for (Budget budget:budgets){
            BudgetWithoutAccountAndOwnerDTO responseTransaction=new BudgetWithoutAccountAndOwnerDTO(budget);
            responseBudgets.add(responseTransaction);
        }
        return responseBudgets;
    }

    public BudgetWithoutAccountAndOwnerDTO delete(int budgetId , int ownerId) {
        Optional<Budget> optionalBudget=budgetRepository.findById(budgetId);
        if (optionalBudget.isEmpty()){
            throw new  NotFoundException("Budget doesn't exist");
        }
        Budget budget=optionalBudget.get();
        if (budget.getOwner().getId()!=ownerId){
            throw new NotFoundException("You don't own budget with such Id");
        }
        BudgetWithoutAccountAndOwnerDTO responseBudget=new BudgetWithoutAccountAndOwnerDTO(budget);
        budgetRepository.deleteById(budgetId);
        return responseBudget;
    }

    public BudgetWithoutAccountAndOwnerDTO addBudgetToAcc(int userId, CreateBudgetRequestDTO dto) {
        Optional<Account> optionalAccount= accountRepository.findById(dto.getAccountId());
        Optional<User> optionalUser=userRepository.findById(userId);
        Optional<Category> optionalCategory=categoryRepository.findById(dto.getCategoryId());
        if (optionalAccount.isEmpty()){
            throw new  NotFoundException("Account doesn't exist");
        }
        if (optionalUser.isEmpty()){
            throw new NotFoundException("User doesn't exist");
        }
        if (optionalCategory.isEmpty()){
            throw new NotFoundException("Category doesn't exist");
        }
        Budget budget=new Budget(dto);
        Account account=optionalAccount.get();
        //validation that check is account owner is current user
        if(account.getOwner().getId()!=userId){
            throw new BadRequestException("You can add budgets only to you ");
        }
        User owner=optionalUser.get();
        Category category=optionalCategory.get();
        budget.setName(dto.getName());
        budget.setAmount(dto.getAmount());
        budget.setDueTime(dto.getDueTime());
        budget.setOwner(owner);
        budget.setAccount(account);
        budget.setCategory(category);
        budgetRepository.save(budget);
        return new BudgetWithoutAccountAndOwnerDTO(budget);
    }

    public BudgetWithoutAccountAndOwnerDTO editBudget(int budgetId, CreateBudgetRequestDTO dto, int userId) {
        Optional<Budget> optionalBudget=budgetRepository.findById(budgetId);
        Optional<Account> optionalAccount=accountRepository.findById(dto.getAccountId());
        Optional<Category> optionalCategory=categoryRepository.findById(dto.getCategoryId());
        if (optionalBudget.isEmpty()){
            throw new NotFoundException("Budget doesn't exist");
        }
        if (optionalAccount.isEmpty()){
            throw new NotFoundException("Account doesn't exist");
        }
        if (optionalCategory.isEmpty()){
            throw new NotFoundException("Category doesn't exist");
        }
        Budget budget= optionalBudget.get();
        Account account=optionalAccount.get();
        Category category=optionalCategory.get();
        if (account.getOwner().getId()!=userId){
            throw new BadRequestException("You can't set budget to account that you aren't owner");
        }
        if (dto.getName()!=null){
            budget.setName(dto.getName());
        }
        if (dto.getAmount()>0){
            budget.setAmount(dto.getAmount());
        }
        if (dto.getDueTime()!=null){
            budget.setDueTime(dto.getDueTime());
        }
        if (dto.getLabel()!=null){
            budget.setLabel(dto.getLabel());
        }
        budget.setAccount(account);
        budget.setCategory(category);
        budgetRepository.save(budget);
        return new BudgetWithoutAccountAndOwnerDTO(budget);
    }

    public double getSpendings(int categoryId) {
        Optional<Category> category=categoryRepository.findById(categoryId);
        if (category.isEmpty()){
            throw new NotFoundException("There is not category with such ID");
        }
        ArrayList<Budget> budgets=budgetRepository.findBudgetsByCategoryId(categoryId);
        if (budgets.isEmpty()){
            throw new NotFoundException("This account don't have budgets corresponding to this category");
        }
        double totalSpends=0;
        for (Budget budget:budgets) {
            totalSpends+=budget.getAmount();
        }
        return totalSpends;
    }

    public ArrayList<BudgetWithoutAccountAndOwnerDTO> filter(int userId, FilterBudgetRequestDTO dto) {
         return budgetDao.filterBudget(userId,dto);
    }
}
