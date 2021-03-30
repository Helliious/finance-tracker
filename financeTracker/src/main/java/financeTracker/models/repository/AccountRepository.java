package financeTracker.models.repository;

import financeTracker.models.pojo.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findAccountByNameAndOwnerId(String name, int ownerId);
    Account findByIdAndOwnerId(int accountId, int ownerId);
    List<Account> findAllByOwnerId(int ownerId);
}
