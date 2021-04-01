package financeTracker.models.dao;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.planned_payment_dto.FilterPlannedPaymentRequestDTO;
import financeTracker.models.pojo.*;
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
public class PlannedPaymentDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    public List<PlannedPayment> filter(int userId, FilterPlannedPaymentRequestDTO plannedPaymentRequestDTO) {
        List<PlannedPayment> plannedPayments = new ArrayList<>();
        StringBuilder sql =new StringBuilder("SELECT * FROM planned_payments WHERE owner_id = ?");
        boolean nameIncludedInFilter = false;
        boolean paymentTypeIncluded = false;
        boolean frequencyIncluded = false;
        boolean bothAmountsIncluded = false;
        boolean amountFromIncluded = false;
        boolean amountToIncluded = false;
        boolean bothDatesIncluded = false;
        boolean dateFromIncluded = false;
        boolean dateToIncluded = false;
        if (plannedPaymentRequestDTO.getName() != null) {
            sql.append("AND name LIKE ?");
            nameIncludedInFilter = true;
        }
        if (plannedPaymentRequestDTO.getPaymentType() != null){
            sql.append("AND payment_type = ?");
            paymentTypeIncluded = true;
        }
        if (plannedPaymentRequestDTO.getFrequency() != null && plannedPaymentRequestDTO.getDurationUnit() != null) {
            sql.append("AND frequency = ? AND duration_unit = ?");
            frequencyIncluded = true;
        }
        if (plannedPaymentRequestDTO.getAmountTo() != null && plannedPaymentRequestDTO.getAmountFrom() > plannedPaymentRequestDTO.getAmountTo()) {
            throw new BadRequestException("Amount from can't be bigger than Amount to!");
        }
        if (plannedPaymentRequestDTO.getAmountFrom() != null && plannedPaymentRequestDTO.getAmountTo() != null) {
            if (plannedPaymentRequestDTO.getAmountFrom() < plannedPaymentRequestDTO.getAmountTo()) {
                sql.append("AND amount BETWEEN ? AND ? ");
                bothAmountsIncluded = true;
            } else {
                throw new BadRequestException("Entered invalid amount range!");
            }
        }
        else {
            if (plannedPaymentRequestDTO.getAmountFrom() != null && plannedPaymentRequestDTO.getAmountTo() == null) {
                sql.append("AND amount > ? ");
                amountFromIncluded = true;
            }
            if (plannedPaymentRequestDTO.getAmountFrom() == null && plannedPaymentRequestDTO.getAmountTo() != null) {
                sql.append("AND amount < ? ");
                amountToIncluded = true;
            }
        }
        if(plannedPaymentRequestDTO.getDueTimeFrom() != null && plannedPaymentRequestDTO.getDueTimeTo() != null) {
            if (plannedPaymentRequestDTO.getDueTimeFrom().compareTo(plannedPaymentRequestDTO.getDueTimeTo()) < 0) {
                sql.append("AND due_time BETWEEN ? AND ?");
                bothDatesIncluded = true;
            } else {
                throw new BadRequestException("Entered invalid due time range!");
            }
        }
        else{
            if (plannedPaymentRequestDTO.getDueTimeFrom() != null && plannedPaymentRequestDTO.getDueTimeTo() == null) {
                sql.append("AND due_time >= ?");
                dateFromIncluded = true;
            }
            if (plannedPaymentRequestDTO.getDueTimeFrom() == null && plannedPaymentRequestDTO.getDueTimeTo() != null) {
                sql.append("AND due_time <= ?");
                dateToIncluded = true;
            }
        }
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIdx = 1;
            ps.setInt(paramIdx++, userId);
            if (nameIncludedInFilter) {
                ps.setString(paramIdx++, plannedPaymentRequestDTO.getName() + "%");
            }
            if (paymentTypeIncluded) {
                ps.setString(paramIdx++, plannedPaymentRequestDTO.getPaymentType());
            }
            if (frequencyIncluded) {
                ps.setInt(paramIdx++, plannedPaymentRequestDTO.getFrequency());
                ps.setString(paramIdx++, plannedPaymentRequestDTO.getDurationUnit());
            }
            if (bothAmountsIncluded) {
                ps.setDouble(paramIdx++, plannedPaymentRequestDTO.getAmountFrom());
                ps.setDouble(paramIdx++, plannedPaymentRequestDTO.getAmountTo());
            }
            if (amountFromIncluded) {
                ps.setDouble(paramIdx++, plannedPaymentRequestDTO.getAmountFrom());
            }
            if (amountToIncluded) {
                ps.setDouble(paramIdx++, plannedPaymentRequestDTO.getAmountTo());
            }
            if(bothDatesIncluded){
                ps.setTimestamp(paramIdx++, plannedPaymentRequestDTO.getDueTimeFrom());
                ps.setTimestamp(paramIdx++, plannedPaymentRequestDTO.getDueTimeTo());
            }
            if(dateFromIncluded){
                ps.setTimestamp(paramIdx++, plannedPaymentRequestDTO.getDueTimeFrom());
            }
            if(dateToIncluded){
                ps.setTimestamp(paramIdx, plannedPaymentRequestDTO.getDueTimeTo());
            }
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                do{
                    Optional<Account> optionalAccount = accountRepository.findById(result.getInt("account_id"));
                    if (optionalAccount.isEmpty()) {
                        throw new NotFoundException("Account not found!");
                    }
                    Optional<Category> optionalCategory = categoryRepository.findById(result.getInt("category_id"));
                    if (optionalCategory.isEmpty()) {
                        throw new NotFoundException("Category not found!");
                    }
                    Optional<User> optionalUser = userRepository.findById(result.getInt("owner_id"));
                    if (optionalUser.isEmpty()) {
                        throw new NotFoundException("User not found!");
                    }
                    PlannedPayment plannedPayment = new PlannedPayment(result.getInt("id"),
                            result.getString("name"),
                            result.getString("payment_type"),
                            result.getInt("frequency"),
                            result.getString("duration_unit"),
                            result.getDouble("amount"),
                            result.getTimestamp("due_time"),
                            result.getString("description"),
                            optionalAccount.get(),
                            optionalCategory.get(),
                            optionalUser.get()
                    );
                    plannedPayments.add(plannedPayment);
                }while (result.next());
            } else {
                throw new NotFoundException("Planned payment not found!");
            }
        } catch (SQLException e) {
            throw new BadRequestException("Connection error, reason - " + e.getMessage());
        }
        return plannedPayments;
    }
}
