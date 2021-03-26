package financeTracker.models.repository;

import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findAccountByNameAndOwner(String name, User user);
}
