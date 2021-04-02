package financeTracker.models.dao;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.account_dto.FilterAccountRequestDTO;
import financeTracker.models.pojo.*;
import financeTracker.models.repository.BudgetRepository;
import financeTracker.models.repository.PlannedPaymentsRepository;
import financeTracker.models.repository.TransactionRepository;
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
public class AccountDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PlannedPaymentsRepository plannedPaymentsRepository;
    @Autowired
    private BudgetRepository budgetRepository;

    public List<Account> filter(int userId, FilterAccountRequestDTO accountRequestDTO) {
        List<Account> accounts = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM accounts WHERE owner_id = ?");
        boolean nameIncludedInFilter = false;
        boolean bothBalanceIncluded = false;
        boolean balanceFromIncluded = false;
        boolean balanceToIncluded = false;
        boolean bothAccLimitIncluded = false;
        boolean accLimitFromIncluded = false;
        boolean accLimitToIncluded = false;
        boolean bothCreateTimesIncluded = false;
        boolean createTimeFromIncluded = false;
        boolean createTimeToIncluded = false;
        if (accountRequestDTO.getName() != null) {
            sql.append("AND name LIKE ?") ;
            nameIncludedInFilter = true;
        }
        if (accountRequestDTO.getBalanceTo() != null && accountRequestDTO.getBalanceFrom() > accountRequestDTO.getBalanceTo()) {
            throw new BadRequestException("Balance from can't be bigger than Balance to!");
        }
        if (accountRequestDTO.getBalanceFrom() != null && accountRequestDTO.getBalanceTo() != null) {
            if (accountRequestDTO.getBalanceFrom() < accountRequestDTO.getBalanceTo()) {
                sql.append("AND amount BETWEEN ? AND ? ");
                bothBalanceIncluded = true;
            } else {
                throw new BadRequestException("Entered invalid balance range!");
            }
        }
        else {
            if (accountRequestDTO.getBalanceFrom() != null && accountRequestDTO.getBalanceTo() == null) {
                sql.append("AND amount > ? ");
                balanceFromIncluded = true;
            }
            if (accountRequestDTO.getBalanceFrom() == null && accountRequestDTO.getBalanceTo() != null) {
                sql.append("AND amount < ? ");
                balanceToIncluded = true;
            }
        }
        if (accountRequestDTO.getAccLimitFrom() != null && accountRequestDTO.getAccLimitTo() != null) {
            if (accountRequestDTO.getAccLimitFrom() < accountRequestDTO.getAccLimitTo()) {
                sql.append("AND amount BETWEEN ? AND ? ");
                bothAccLimitIncluded = true;
            } else {
                throw new BadRequestException("Entered invalid account limit range!");
            }
        }
        else {
            if (accountRequestDTO.getAccLimitFrom() != null && accountRequestDTO.getAccLimitTo() == null) {
                sql.append("AND amount > ? ");
                accLimitFromIncluded = true;
            }
            if (accountRequestDTO.getAccLimitFrom() == null && accountRequestDTO.getAccLimitTo() != null) {
                sql.append("AND amount < ? ");
                accLimitToIncluded = true;
            }
        }
        if(accountRequestDTO.getCreateTimeFrom() != null && accountRequestDTO.getCreateTimeTo() != null) {
            if (accountRequestDTO.getCreateTimeFrom().compareTo(accountRequestDTO.getCreateTimeTo()) < 0) {
                sql.append("AND create_time BETWEEN ? AND ?");
                bothCreateTimesIncluded = true;
            } else {
                throw new BadRequestException("Entered invalid create time range!");
            }
        }
        else{
            if (accountRequestDTO.getCreateTimeFrom() != null && accountRequestDTO.getCreateTimeTo() == null) {
                sql.append("AND create_time >= ?");
                createTimeFromIncluded = true;
            }
            if (accountRequestDTO.getCreateTimeFrom() == null && accountRequestDTO.getCreateTimeTo() != null) {
                sql.append("AND create_time <= ?");
                createTimeToIncluded = true;
            }
        }
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIdx = 1;
            ps.setInt(paramIdx++, userId);
            if (nameIncludedInFilter) {
                ps.setString(paramIdx++, accountRequestDTO.getName() + "%");
            }
            if (bothBalanceIncluded) {
                ps.setDouble(paramIdx++, accountRequestDTO.getBalanceFrom());
                ps.setDouble(paramIdx++, accountRequestDTO.getBalanceTo());
            }
            if (balanceFromIncluded) {
                ps.setDouble(paramIdx++, accountRequestDTO.getBalanceFrom());
            }
            if (balanceToIncluded) {
                ps.setDouble(paramIdx++, accountRequestDTO.getBalanceTo());
            }
            if(bothAccLimitIncluded){
                ps.setDouble(paramIdx++, accountRequestDTO.getAccLimitFrom());
                ps.setDouble(paramIdx++, accountRequestDTO.getAccLimitTo());
            }
            if (accLimitFromIncluded) {
                ps.setDouble(paramIdx++, accountRequestDTO.getAccLimitFrom());
            }
            if (accLimitToIncluded) {
                ps.setDouble(paramIdx++, accountRequestDTO.getAccLimitTo());
            }
            if(bothCreateTimesIncluded){
                ps.setTimestamp(paramIdx++, accountRequestDTO.getCreateTimeFrom());
                ps.setTimestamp(paramIdx++, accountRequestDTO.getCreateTimeTo());
            }
            if(createTimeFromIncluded){
                ps.setTimestamp(paramIdx++, accountRequestDTO.getCreateTimeFrom());
            }
            if(createTimeToIncluded){
                ps.setTimestamp(paramIdx, accountRequestDTO.getCreateTimeTo());
            }
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                do{
                    Optional<User> optionalUser = userRepository.findById(result.getInt("owner_id"));
                    if (optionalUser.isEmpty()) {
                        throw new NotFoundException("User not found!");
                    }
                    List<Transaction> transactions = transactionRepository.findTransactionsByAccountId(result.getInt("id"));
                    List<PlannedPayment> plannedPayments = plannedPaymentsRepository.findAllByAccountId(result.getInt("id"));
                    List<Budget> budgets = budgetRepository.findBudgetsByAccountId(result.getInt("id"));
                    Account account = new Account(result.getInt("id"),
                                                result.getString("name"),
                                                result.getDouble("balance"),
                                                result.getDouble("acc_limit"),
                                                result.getTimestamp("create_time"),
                                                optionalUser.get(),
                                                transactions,
                                                plannedPayments,
                                                budgets
                                                );
                    accounts.add(account);
                }while (result.next());
            } else {
                throw new NotFoundException("Account not found!");
            }
        } catch (SQLException e) {
            throw new BadRequestException("Connection error, reason - " + e.getMessage());
        }
        return accounts;
    }
}
