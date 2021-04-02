package financeTracker.models.repository;

import financeTracker.models.pojo.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface BudgetRepository extends JpaRepository<Budget,Integer>{
    ArrayList<Budget> findBudgetsByOwnerId(int ownerId);
    ArrayList<Budget> findBudgetsByAccountId(int accountId);
    ArrayList<Budget> findBudgetsByOwnerIdAndCategoryId(int ownerId,int categoryId);
    Budget findByIdAndOwnerId(int id, int ownerId);
    Budget findByIdAndOwnerIdAndAccountId(int id, int ownerId, int accountId);
}