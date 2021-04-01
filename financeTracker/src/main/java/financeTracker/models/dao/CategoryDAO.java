package financeTracker.models.dao;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.category_dto.CategoryExpensesDTO;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CategoryDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;

    public List<CategoryExpensesDTO> referenceOverallExpensesByCategory(int userId) {
        List<CategoryExpensesDTO> expenses = new ArrayList<>();
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        StringBuilder sql = new StringBuilder("SELECT c.name, COALESCE(SUM(t.amount),0) + COALESCE(SUM(p.amount),0) + COALESCE(SUM(b.amount),0) AS expense " +
                                        "FROM categories c " +
                                        "JOIN users u ON c.owner_id = u.id AND u.username LIKE ? " +
                                        "LEFT OUTER JOIN transactions t ON c.id = t.category_id " +
                                        "LEFT OUTER JOIN planned_payments p ON c.id = p.category_id " +
                                        "LEFT OUTER JOIN budgets b ON c.id = b.category_id " +
                                        "GROUP BY c.name");
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setString(1, optUser.get().getUsername());
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                do{
                    CategoryExpensesDTO expense = new CategoryExpensesDTO(
                            result.getString("name"),
                            result.getDouble("expense")
                    );
                    expenses.add(expense);
                }while (result.next());
            } else {
                throw new NotFoundException("No data for expenses by category!");
            }
        } catch (SQLException e) {
            throw new BadRequestException("Connection error, reason - " + e.getMessage());
        }
        return expenses;
    }
}
