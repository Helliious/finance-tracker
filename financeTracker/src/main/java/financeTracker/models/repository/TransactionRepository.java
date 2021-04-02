package financeTracker.models.repository;

import financeTracker.models.pojo.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findAllByOwnerId(int ownerId);
    List<Transaction> findAllByAccountId(int accountId);
    Transaction findByIdAndOwnerId(int transactionId, int ownerId);
    Transaction findByIdAndOwnerIdAndAccountId(int transactionId, int ownerId, int accountId);
}
