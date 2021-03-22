package financeTracker.models.users;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class UserDao {
    public User getById(int id) {
        return new User(1, "Stoqn", "Tomicin", "helious", "1234", "stc@abv.bg", new Timestamp(System.currentTimeMillis()));
    }
}
