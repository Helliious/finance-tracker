package financeTracker.models.repository;

import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {

}
