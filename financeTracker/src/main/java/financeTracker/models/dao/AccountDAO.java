package financeTracker.models.dao;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.account_dto.FilterAccountRequestDTO;
import financeTracker.models.pojo.*;
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

    public List<AccountWithoutOwnerDTO> filter(int userId, FilterAccountRequestDTO accountRequestDTO) {
        List<AccountWithoutOwnerDTO> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE owner_id = ?";
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
            sql += "AND name LIKE ?";
            nameIncludedInFilter = true;
        }
        if (accountRequestDTO.getBalanceFrom() > accountRequestDTO.getBalanceTo() && accountRequestDTO.getBalanceTo() != 0) {
            throw new BadRequestException("Amount from can't be bigger than Amount to");
        }
        if (accountRequestDTO.getBalanceFrom() > 0 && accountRequestDTO.getBalanceTo() > 0) {
            //TODO: check if balance from > than balance to
            sql += "AND amount BETWEEN ? AND ? ";
            bothBalanceIncluded = true;
        }
        else {
            if (accountRequestDTO.getBalanceFrom() > 0 && accountRequestDTO.getBalanceTo() <= 0) {
                sql += "AND amount > ? ";
                balanceFromIncluded = true;
            }
            if (accountRequestDTO.getBalanceFrom() <= 0 && accountRequestDTO.getBalanceTo() > 0) {
                sql += "AND amount < ? ";
                balanceToIncluded = true;
            }
        }
        if (accountRequestDTO.getAccLimitFrom() > 0 && accountRequestDTO.getAccLimitTo() > 0) {
            //TODO: check if acc limit from > than acc limit to
            sql += "AND amount BETWEEN ? AND ? ";
            bothAccLimitIncluded = true;
        }
        else {
            if (accountRequestDTO.getAccLimitFrom() > 0 && accountRequestDTO.getAccLimitTo() <= 0) {
                sql += "AND amount > ? ";
                accLimitFromIncluded = true;
            }
            if (accountRequestDTO.getAccLimitFrom() <= 0 && accountRequestDTO.getAccLimitTo() > 0) {
                sql += "AND amount < ? ";
                accLimitToIncluded = true;
            }
        }
        if(accountRequestDTO.getCreateTimeFrom() != null && accountRequestDTO.getCreateTimeTo() != null) {
            //TODO: check if timeFrom is > than timeTo
            sql += "AND create_time BETWEEN ? AND ?";
            bothCreateTimesIncluded = true;
        }
        else{
            if (accountRequestDTO.getCreateTimeFrom() != null && accountRequestDTO.getCreateTimeTo() == null) {
                sql += "AND create_time >= ?";
                createTimeFromIncluded = true;
            }
            if (accountRequestDTO.getCreateTimeFrom() == null && accountRequestDTO.getCreateTimeTo() != null) {
                sql += "AND create_time <= ?";
                createTimeToIncluded = true;
            }
        }
        System.out.println(sql);
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
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
                    List<Transaction> transactions = transactionRepository.findTransactionsByAccount_Id(result.getInt("id"));
                    List<PlannedPayment> plannedPayments = plannedPaymentsRepository.findAllByAccountId(result.getInt("id"));
                    Account account = new Account(result.getInt("id"),
                                                result.getString("name"),
                                                result.getDouble("balance"),
                                                result.getDouble("acc_limit"),
                                                result.getTimestamp("create_time"),
                                                optionalUser.get(),
                                                transactions,
                                                plannedPayments
                                                );
                    accounts.add(new AccountWithoutOwnerDTO(account));
                }while (result.next());
            } else {
                throw new NotFoundException("Account not found");
            }
        } catch (SQLException e) {
            throw new BadRequestException("Connection error, reason - " + e.getMessage());
        }
        return accounts;
    }
}
