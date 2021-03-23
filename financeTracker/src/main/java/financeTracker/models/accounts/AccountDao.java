package financeTracker.models.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class AccountDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Account getById(int id) throws Exception {
        String sql = "SELECT id, name, balance, acc_limit, create_time FROM accounts WHERE id = ?";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return new Account(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getDouble("balance"),
                        result.getDouble("acc_limit"),
                        result.getTimestamp("create_time")
                );
            }
            throw new Exception("Account not found");
        }
    }
}
