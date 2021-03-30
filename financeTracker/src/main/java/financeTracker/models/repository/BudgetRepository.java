package financeTracker.models.repository;

import financeTracker.models.pojo.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface BudgetRepository extends JpaRepository<Budget,Integer>{
 ArrayList<Budget> findBudgetsByOwnerId(int ownerId);
 ArrayList<Budget> findBudgetsByAccountId(int accountId);
 ArrayList<Budget> findBudgetsByOwnerIdAndCategoryId(int ownerid,int categoryId);
}