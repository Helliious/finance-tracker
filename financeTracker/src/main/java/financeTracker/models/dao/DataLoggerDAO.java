package financeTracker.models.dao;

import financeTracker.exceptions.BadRequestException;
import financeTracker.models.pojo.DataLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class DataLoggerDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<DataLogger> logData() {
        List<DataLogger> logs = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT u.id,\n" +
                "u.username,\n" +
                "IFNULL(SUM(DISTINCT a.balance), 0) AS overall_balance,\n" +
                "COUNT(DISTINCT a.id) AS accounts_count,\n" +
                "COUNT(DISTINCT t.id) AS transactions_count,\n" +
                "COUNT(DISTINCT p.id) AS planned_payments_count,\n" +
                "COUNT(DISTINCT b.id) AS budgets_count\n" +
                "FROM users u\n" +
                "LEFT OUTER JOIN accounts a ON u.id = a.owner_id\n" +
                "LEFT OUTER JOIN transactions t ON u.id = t.owner_id\n" +
                "LEFT OUTER JOIN planned_payments p ON u.id = p.owner_id\n" +
                "LEFT OUTER JOIN budgets b ON u.id = b.owner_id\n" +
                "GROUP BY u.username;");

        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                do{
                    DataLogger log = new DataLogger(
                            result.getInt("id"),
                            result.getString("username"),
                            result.getDouble("overall_balance"),
                            result.getInt("accounts_count"),
                            result.getInt("transactions_count"),
                            result.getInt("planned_payments_count"),
                            result.getInt("budgets_count")
                    );
                    logs.add(log);
                }while (result.next());
            }
        } catch (SQLException e) {
            throw new BadRequestException("Connection error, reason - " + e.getMessage());
        }
        return logs;
    }
}
