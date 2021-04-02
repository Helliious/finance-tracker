package financeTracker.models.repository;

import financeTracker.models.pojo.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget,Integer>{
    List<Budget> findBudgetsByOwnerId(int ownerId);
    List<Budget> findBudgetsByAccountId(int accountId);
    Budget findByIdAndOwnerId(int id, int ownerId);
    Budget findByIdAndOwnerIdAndAccountId(int id, int ownerId, int accountId);
    List<Budget> findAllByOwnerIdAndAccountId(int ownerId, int accountId);
}