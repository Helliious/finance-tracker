package financeTracker.models.repository;

import financeTracker.models.pojo.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {

}
