package financeTracker.models.repository;

import financeTracker.models.pojo.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import financeTracker.models.pojo.User;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findAccountByNameAndOwner(String name, User user);
    Account findByIdAndOwnerId(int accountId, int ownerId);
}
