package financeTracker.models.dao;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.models.pojo.*;
import financeTracker.models.repository.AccountRepository;
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
import java.util.Objects;
import java.util.Optional;

@Component
public class BudgetDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    public List<Budget> filterBudget(int userId, FilterBudgetRequestDTO dto) {
        List<Budget> budgets = new ArrayList<>();
        StringBuilder sql =new StringBuilder("SELECT * FROM budgets WHERE owner_id = ? ");
        boolean nameIncludedInFilter = false;
        boolean bothAmountsIncluded = false;
        boolean amountFromIncluded = false;
        boolean amountToIncluded = false;
        boolean bothCreateTimesIncluded = false;
        boolean createTimeFromIncluded = false;
        boolean createTimeToIncluded = false;
        boolean bothDueTimesIncluded = false;
        boolean dueTimeFromIncluded = false;
        boolean dueTimeToIncluded = false;
        if (dto.getName() != null) {
            sql.append("AND name LIKE ?");
            nameIncludedInFilter = true;
        }
        if (dto.getAmountFrom() != null && dto.getAmountTo() != null) {
            if (dto.getAmountFrom() > dto.getAmountTo()) {
                throw new BadRequestException("Amount from can't be bigger than Amount to");
            }
            sql.append("AND amount BETWEEN ? AND ? ");
            bothAmountsIncluded = true;
        }
        else {
            if (dto.getAmountFrom() != null && dto.getAmountTo() == null) {
                sql.append("AND amount > ? ");
                amountFromIncluded = true;
            }
            if (dto.getAmountFrom() == null && dto.getAmountTo() != null) {
                sql.append("AND amount < ? ");
                amountToIncluded = true;
            }
        }
        if(dto.getCreateTimeFrom() != null && dto.getCreateTimeTo() != null) {
            if (dto.getCreateTimeFrom().compareTo(dto.getCreateTimeTo()) > 0) {
                throw new BadRequestException("Invalid create time range");
            }
            sql.append("AND create_time BETWEEN ? AND ? ");
            bothCreateTimesIncluded = true;
        }
        else{
            if (dto.getCreateTimeFrom() != null && dto.getCreateTimeTo() == null) {
                sql.append("AND create_time >= ? ");
                createTimeFromIncluded = true;
            }
            if (dto.getCreateTimeFrom() == null && dto.getCreateTimeTo() != null) {
                sql.append("AND create_time <= ? ");
                createTimeToIncluded = true;
            }
        }
        if(dto.getDueTimeFrom() != null && dto.getDueTimeTo() != null) {
            if (dto.getDueTimeFrom().compareTo(dto.getDueTimeTo()) > 0) {
                throw new BadRequestException("Invalid due time range");
            }
            sql.append("AND due_time BETWEEN ? AND ? ");
            bothDueTimesIncluded = true;
        }
        else{
            if (dto.getDueTimeFrom() != null && dto.getDueTimeTo() == null) {
                sql.append("AND due_time >= ? ");
                dueTimeFromIncluded = true;
            }
            if (dto.getDueTimeFrom() == null && dto.getDueTimeTo() != null) {
                sql.append("AND due_time <= ? ");
                dueTimeToIncluded = true;
            }
        }
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            int paramIdx = 1;
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            ps.setInt(paramIdx++, userId);
            if (nameIncludedInFilter) {
                ps.setString(paramIdx++, dto.getName() + "%");
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
            if (bothCreateTimesIncluded) {
                ps.setTimestamp(paramIdx++, dto.getCreateTimeFrom());
                ps.setTimestamp(paramIdx++, dto.getCreateTimeTo());
            }
            if (createTimeFromIncluded) {
                ps.setTimestamp(paramIdx++, dto.getCreateTimeFrom());
            }
            if (createTimeToIncluded) {
                ps.setTimestamp(paramIdx, dto.getCreateTimeTo());
            }
            if (bothDueTimesIncluded) {
                ps.setTimestamp(paramIdx++, dto.getDueTimeFrom());
                ps.setTimestamp(paramIdx++, dto.getDueTimeTo());
            }
            if (dueTimeFromIncluded) {
                ps.setTimestamp(paramIdx++, dto.getDueTimeFrom());
            }
            if (dueTimeToIncluded) {
                ps.setTimestamp(paramIdx, dto.getDueTimeTo());
            }
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                do {
                    Optional<Account> optionalAccount = accountRepository.findById(result.getInt("account_id"));
                    Optional<User> optionalUser = userRepository.findById(result.getInt("owner_id"));
                    if (optionalAccount.isEmpty()) {
                        throw new NotFoundException("Account not found!");
                    }
                    if (optionalUser.isEmpty()) {
                        throw new NotFoundException("User not found!");
                    }
                    Budget budget = new Budget(result.getInt("id"),
                            result.getString("name"),
                            result.getString("label"),
                            result.getDouble("amount"),
                            result.getTimestamp("create_time"),
                            result.getTimestamp("due_time"),
                            result.getString("description"),
                            optionalAccount.get(),
                            optionalUser.get(),
                            null
                    );
                    budgets.add(budget);
                } while (result.next());
            }
        }
        catch (SQLException e){
            throw new BadRequestException("Connection error, reason - " + e.getMessage());
        }
        return budgets;
    }
}
