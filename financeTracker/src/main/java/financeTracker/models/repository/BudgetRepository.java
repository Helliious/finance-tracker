package financeTracker.models.repository;

import financeTracker.models.pojo.Budget;
import financeTracker.models.pojo.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface BudgetRepository extends JpaRepository<Budget,Integer>{
 ArrayList<Budget> findBudgetsByOwnerId(int ownerId);
 ArrayList<Budget> findBudgetsByAccountId(int accountId);
 ArrayList<Budget> findBudgetsByCategoryId(int categoryId);

}