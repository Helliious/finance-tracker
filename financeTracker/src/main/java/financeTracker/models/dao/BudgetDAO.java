package financeTracker.models.dao;
import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.budget_dto.BudgetWithoutAccountAndOwnerDTO;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Budget;
import financeTracker.models.pojo.Category;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.CategoryRepository;
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
public class BudgetDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountRepository accountRepository;

    public List<Budget> filterBudget(int userId, FilterBudgetRequestDTO dto) {
        List<Budget> budgets = new ArrayList<>();
        StringBuilder sql =new StringBuilder("SELECT * FROM budgets WHERE owner_id = ? ");
        boolean nameIncludedInFilter = false;
        boolean categoryIncludedInFilter = false;
        boolean bothAmountsIncluded = false;
        boolean amountFromIncluded = false;
        boolean amountToIncluded = false;
        boolean bothDatesIncluded = false;
        boolean dateFromIncluded = false;
        boolean dateToIncluded = false;
        if (dto.getName() != null) {
            sql.append("AND name LIKE ?");
            nameIncludedInFilter = true;
        }
        if (dto.getCategoryId() > 0) {
            sql.append("AND category_id = ?");
            categoryIncludedInFilter = true;
        }
        if (dto.getAmountFrom() > dto.getAmountTo() && dto.getAmountTo() != 0) {
            throw new BadRequestException("Amount from can't be bigger than Amount to");
        }
        if (dto.getAmountFrom() > 0 && dto.getAmountTo() > 0) {
            sql.append("AND amount BETWEEN ? AND ? ");
            bothAmountsIncluded = true;
        }
        else {
            if (dto.getAmountFrom() > 0 && dto.getAmountTo() <= 0) {
                sql.append("AND amount > ? ");
                amountFromIncluded = true;
            }
            if (dto.getAmountFrom() <= 0 && dto.getAmountTo() > 0) {
                amountToIncluded = true;
                sql.append("AND amount < ? ");
            }
        }
        if(dto.getDateFrom() != null && dto.getDateTo() != null) {
            sql.append("AND create_time BETWEEN ? AND ?");
            bothDatesIncluded = true;
        }
        else{
            if (dto.getDateFrom() != null && dto.getDateTo() == null) {
                sql.append("AND create_time >= ?");
                dateFromIncluded = true;
            }
            if (dto.getDateFrom() == null && dto.getDateTo() != null) {
                sql.append("AND create_time <= ?");
                dateToIncluded = true;
            }
        }
        System.out.println(sql);
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            int paramIdx = 1;
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            ps.setInt(paramIdx++, userId);
            if (nameIncludedInFilter) {
                ps.setString(paramIdx++, dto.getName() + "%");
            }
            if (categoryIncludedInFilter) {
//                System.out.println(i + " setted " + dto.getCategoryId());
                ps.setInt(paramIdx++, dto.getCategoryId());
            }
            if (bothAmountsIncluded) {
                ps.setDouble(paramIdx++, dto.getAmountFrom());
                ps.setDouble(paramIdx++, dto.getAmountTo());
            }
            if (amountFromIncluded) {
                ps.setDouble(paramIdx++, dto.getAmountFrom());
            }
            if (amountToIncluded) {
                ps.setDouble(paramIdx++, dto.getAmountTo());
            }
            if(bothDatesIncluded){
                ps.setTimestamp(paramIdx++, dto.getDateFrom());
                ps.setTimestamp(paramIdx++, dto.getDateTo());
            }
            if(dateFromIncluded){
                ps.setTimestamp(paramIdx++, dto.getDateFrom());
            }
            if(dateToIncluded){
                ps.setTimestamp(paramIdx, dto.getDateTo());
            }
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                do {
                    Optional<Account> optionalAccount = accountRepository.findById(result.getInt("account_id"));
                    Optional<Category> optionalCategory = categoryRepository.findById(result.getInt("category_id"));
                    Optional<User> optionalUser = userRepository.findById(result.getInt("owner_id"));
                    Budget budget = new Budget(result.getInt("id"),
                            result.getString("name"),
                            result.getString("label"),
                            result.getDouble("amount"),
                            result.getTimestamp("due_time"),
                            optionalAccount.get(),
                            optionalUser.get(),
                            optionalCategory.get()
                    );
                    budgets.add(budget);
                } while (result.next());
            }
            else{
                throw new NotFoundException("There is not budgets corresponding to current filter");
            }
        }
        catch (SQLException e){
            e.getMessage();
        }
        return budgets;
    }
}
