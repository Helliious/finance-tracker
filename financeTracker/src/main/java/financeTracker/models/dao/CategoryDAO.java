package financeTracker.models.dao;

import financeTracker.exceptions.BadRequestException;
import financeTracker.models.dto.category_dto.CategoryExpensesDTO;
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
public class CategoryDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<CategoryExpensesDTO> referenceOverallExpensesByCategory() {
        List<CategoryExpensesDTO> expenses = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT u.username, c.name AS category, COALESCE(SUM(t.amount), 0) + COALESCE(SUM(p.amount), 0) AS expense FROM users u\n" +
                "LEFT OUTER JOIN categories c ON c.owner_id IS NULL OR c.owner_id = u.id\n" +
                "LEFT OUTER JOIN transactions t ON u.id = t.owner_id AND c.id = t.category_id AND t.create_time >= NOW() - INTERVAL 1 MONTH\n" +
                "LEFT OUTER JOIN planned_payments p ON u.id = p.owner_id AND c.id = p.category_id AND p.create_time >= NOW() - INTERVAL 1 MONTH\n" +
                "GROUP BY u.username, c.name;");

        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                do{
                    CategoryExpensesDTO expense = new CategoryExpensesDTO(
                            result.getString("username"),
                            result.getString("category"),
                            result.getDouble("expense")
                    );
                    expenses.add(expense);
                }while (result.next());
            }
        } catch (SQLException e) {
            throw new BadRequestException("Connection error, reason - " + e.getMessage());
        }
        return expenses;
    }
}
