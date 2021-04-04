package financeTracker.models.dao;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.planned_payment_dto.FilterPlannedPaymentRequestDTO;
import financeTracker.models.pojo.*;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.UserRepository;
import financeTracker.utils.Validator;
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
        StringBuilder sql = new StringBuilder("SELECT * FROM planned_payments WHERE owner_id = ? ");
        boolean nameIncludedInFilter = false;
        boolean paymentTypeIncluded = false;
        boolean frequencyIncluded = false;
        boolean bothAmountsIncluded = false;
        boolean amountFromIncluded = false;
        boolean amountToIncluded = false;
        boolean bothCreateTimesIncluded = false;
        boolean createTimeFromIncluded = false;
        boolean createTimeToIncluded = false;
        boolean bothDueTimesIncluded = false;
        boolean dueTimeFromIncluded = false;
        boolean dueTimeToIncluded = false;
        if (plannedPaymentRequestDTO.getName() != null) {
            sql.append("AND name LIKE ? ");
            nameIncludedInFilter = true;
        }
        if (plannedPaymentRequestDTO.getPaymentType() != null){
            sql.append("AND payment_type = ? ");
            paymentTypeIncluded = true;
        }
        if (plannedPaymentRequestDTO.getFrequency() != null && plannedPaymentRequestDTO.getDurationUnit() != null) {
            sql.append("AND frequency = ? AND duration_unit = ? ");
            frequencyIncluded = true;
        }
        if (plannedPaymentRequestDTO.getAmountFrom() != null && plannedPaymentRequestDTO.getAmountTo() != null) {
            if (plannedPaymentRequestDTO.getAmountFrom() < plannedPaymentRequestDTO.getAmountTo()) {
                sql.append("AND amount BETWEEN ? AND ? ");
                bothAmountsIncluded = true;
            } else {
                throw new BadRequestException("Invalid amount range");
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
        if(plannedPaymentRequestDTO.getCreateTimeFrom() != null && plannedPaymentRequestDTO.getCreateTimeTo() != null) {
            if (plannedPaymentRequestDTO.getCreateTimeFrom().compareTo(plannedPaymentRequestDTO.getCreateTimeTo()) < 0) {
                sql.append("AND create_time BETWEEN ? AND ? ");
                bothCreateTimesIncluded = true;
            } else {
                throw new BadRequestException("Invalid create time range");
            }
        }
        else{
            if (plannedPaymentRequestDTO.getCreateTimeFrom() != null && plannedPaymentRequestDTO.getCreateTimeTo() == null) {
                sql.append("AND create_time >= ? ");
                createTimeFromIncluded = true;
            }
            if (plannedPaymentRequestDTO.getCreateTimeFrom() == null && plannedPaymentRequestDTO.getCreateTimeTo() != null) {
                sql.append("AND create_time <= ? ");
                createTimeToIncluded = true;
            }
        }
        if(plannedPaymentRequestDTO.getDueTimeFrom() != null && plannedPaymentRequestDTO.getDueTimeTo() != null) {
            if (plannedPaymentRequestDTO.getDueTimeFrom().compareTo(plannedPaymentRequestDTO.getDueTimeTo()) < 0) {
                sql.append("AND due_time BETWEEN ? AND ? ");
                bothDueTimesIncluded = true;
            } else {
                throw new BadRequestException("Invalid due time range");
            }
        }
        else{
            if (plannedPaymentRequestDTO.getDueTimeFrom() != null && plannedPaymentRequestDTO.getDueTimeTo() == null) {
                sql.append("AND due_time >= ? ");
                dueTimeFromIncluded = true;
            }
            if (plannedPaymentRequestDTO.getDueTimeFrom() == null && plannedPaymentRequestDTO.getDueTimeTo() != null) {
                sql.append("AND due_time <= ? ");
                dueTimeToIncluded = true;
            }
        }
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
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
            if (bothCreateTimesIncluded) {
                ps.setTimestamp(paramIdx++, plannedPaymentRequestDTO.getCreateTimeFrom());
                ps.setTimestamp(paramIdx++, plannedPaymentRequestDTO.getCreateTimeTo());
            }
            if (createTimeFromIncluded) {
                ps.setTimestamp(paramIdx++, plannedPaymentRequestDTO.getCreateTimeFrom());
            }
            if (createTimeToIncluded) {
                ps.setTimestamp(paramIdx++, plannedPaymentRequestDTO.getCreateTimeTo());
            }
            if (bothDueTimesIncluded) {
                ps.setTimestamp(paramIdx++, plannedPaymentRequestDTO.getDueTimeFrom());
                ps.setTimestamp(paramIdx++, plannedPaymentRequestDTO.getDueTimeTo());
            }
            if (dueTimeFromIncluded) {
                ps.setTimestamp(paramIdx++, plannedPaymentRequestDTO.getDueTimeFrom());
            }
            if (dueTimeToIncluded) {
                ps.setTimestamp(paramIdx, plannedPaymentRequestDTO.getDueTimeTo());
            }
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                do{
                    Optional<User> optionalUser = userRepository.findById(userId);
                    if (optionalUser.isEmpty()) {
                        throw new NotFoundException("User not found");
                    }
                    Account account = accountRepository.findByIdAndOwnerId(
                            result.getInt("account_id"),
                            userId
                    );
                    Category category = categoryRepository.findByIdAndOwnerId(
                            result.getInt("category_id"),
                            userId
                    );
                    Validator.validateData(account, category);
                    PlannedPayment plannedPayment = new PlannedPayment(result.getInt("id"),
                            result.getString("name"),
                            result.getString("payment_type"),
                            result.getInt("frequency"),
                            result.getString("duration_unit"),
                            result.getDouble("amount"),
                            result.getTimestamp("create_time"),
                            result.getTimestamp("due_time"),
                            result.getString("description"),
                            account,
                            category,
                            optionalUser.get()
                    );
                    plannedPayments.add(plannedPayment);
                }while (result.next());
            }
        } catch (SQLException e) {
            throw new BadRequestException("Connection error, reason - " + e.getMessage());
        }
        return plannedPayments;
    }
}
