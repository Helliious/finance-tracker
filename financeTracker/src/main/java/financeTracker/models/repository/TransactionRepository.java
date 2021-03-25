package financeTracker.models.repository;

import financeTracker.models.pojo.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    ArrayList<Transaction> findTransactionsByUserId(int id);
    ArrayList<Transaction> findTransactionsByAccount_Id(int id);
}
