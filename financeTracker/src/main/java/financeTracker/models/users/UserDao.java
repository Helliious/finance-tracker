package financeTracker.models.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User getById(int id) throws Exception {
        String sql = "SELECT id, first_name, last_name, username, password, email, create_time FROM users WHERE id = ?";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return new User(
                        result.getInt("id"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("email"),
                        result.getTimestamp("create_time")
                );
            }
            throw new Exception("User not found");
        }
    }
}
