package financeTracker.models.dao;

import financeTracker.models.pojo.Budget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class BudgetDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Budget getById(int id) throws Exception {
        String sql = "SELECT id, name, label, amount, due_time FROM budgets WHERE id = ?";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return new Budget(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("label"),
                        result.getDouble("amount"),
                        result.getTimestamp("due_time")
                );
            }
            throw new Exception("Budget not found");
        }
    }
}
