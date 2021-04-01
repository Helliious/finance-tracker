package financeTracker.models.repository;

import financeTracker.models.pojo.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    ArrayList<Transaction> findTransactionsByOwnerId(int id);
    ArrayList<Transaction> findTransactionsByAccountId(int id);
    Transaction findByIdAndOwnerId(int transactionId, int ownerId);
}
