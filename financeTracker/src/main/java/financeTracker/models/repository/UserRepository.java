package financeTracker.models.repository;

import financeTracker.models.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByPassword(String password);
    User findByEmail(String email);

}
