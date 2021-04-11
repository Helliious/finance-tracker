package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.PlannedPaymentDAO;
import financeTracker.models.dto.planned_payment_dto.AddPlannedPaymentDTO;
import financeTracker.models.dto.planned_payment_dto.EditPlannedPaymentDTO;
import financeTracker.models.dto.planned_payment_dto.FilterPlannedPaymentRequestDTO;
import financeTracker.models.pojo.*;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.PlannedPaymentsRepository;
import financeTracker.models.repository.UserRepository;
import financeTracker.utils.Action;
import financeTracker.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PlannedPaymentsService {
    @Autowired
    private PlannedPaymentDAO plannedPaymentDAO;
    @Autowired
    private PlannedPaymentsRepository plannedPaymentsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public PlannedPayment add(AddPlannedPaymentDTO plannedPaymentDTO, int userId, int accountId) {
        plannedPaymentDTO.setDueTime(new Timestamp(System.currentTimeMillis()));
        Optional<User> optUser = userRepository.findById(userId);
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        Category category = categoryRepository.findById(plannedPaymentDTO.getCategoryId());
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        if (account == null) {
            throw new NotFoundException("Account not found");
        }
        Validator.validateCategory(category, userId);
        if (!plannedPaymentDTO.getPaymentType().equals(category.getType())) {
            throw new BadRequestException("Payment type and category type must match");
        }
        User user = optUser.get();
        PlannedPayment plannedPayment = new PlannedPayment(plannedPaymentDTO);
        plannedPayment.setCategory(category);
        user.getPlannedPayments().add(plannedPayment);
        category.getPlannedPayments().add(plannedPayment);
        account.getPlannedPayments().add(plannedPayment);
        ServiceCalculator.calculateBalance(plannedPaymentDTO.getAmount(), plannedPaymentDTO.getPaymentType(), account, Action.ADD);
        plannedPayment.setOwner(user);
        plannedPayment.setAccount(account);
        plannedPayment.setCategory(category);
        return plannedPaymentsRepository.save(plannedPayment);
    }

    public PlannedPayment getById(int accountId, int userId, int plannedPaymentId) {
        PlannedPayment plannedPayment = plannedPaymentsRepository.findPlannedPaymentByIdAndAccountIdAndOwnerId(
                plannedPaymentId,
                accountId,
                userId
        );
        if (plannedPayment == null) {
            throw new NotFoundException("Planned payment not found");
        }
        return plannedPayment;
    }

    public List<PlannedPayment> getAll(int accountId, int userId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        return account.getPlannedPayments();
    }

    public PlannedPayment delete(int accountId, int userId, int plannedPaymentId) {
        PlannedPayment plannedPayment = plannedPaymentsRepository.findPlannedPaymentByIdAndAccountIdAndOwnerId(
                plannedPaymentId,
                accountId,
                userId
        );
        if (plannedPayment == null) {
            throw new NotFoundException("Planned payment not found");
        }
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account != null) {
            if (plannedPayment.getDueTime().compareTo(new Timestamp(System.currentTimeMillis())) < 0) {
                ServiceCalculator.calculateBalance(plannedPayment.getAmount(), plannedPayment.getPaymentType(), account, Action.REMOVE);
            }
        }
        plannedPaymentsRepository.deleteById(plannedPaymentId);
        return plannedPayment;
    }

    public PlannedPayment edit(EditPlannedPaymentDTO editPlannedPaymentDTO, int accountId, int userId, int plannedPaymentId) {
        PlannedPayment plannedPayment = plannedPaymentsRepository.findPlannedPaymentByIdAndAccountIdAndOwnerId(
                plannedPaymentId,
                accountId,
                userId
        );
        if (plannedPayment == null) {
            throw new NotFoundException("Planned payment not found");
        }
        if (editPlannedPaymentDTO.getName() != null) {
            if (plannedPaymentsRepository.findPlannedPaymentByNameAndAccountId(editPlannedPaymentDTO.getName(), accountId) != null) {
                throw new BadRequestException("Planned payment name already exists");
            }
            plannedPayment.setName(editPlannedPaymentDTO.getName());
        }
        if (editPlannedPaymentDTO.getFrequency() != null) {
            if (editPlannedPaymentDTO.getDurationUnit() == null) {
                throw new BadRequestException("Frequency and duration unit must be both set");
            }
            plannedPayment.setFrequency(editPlannedPaymentDTO.getFrequency());
        }
        if (editPlannedPaymentDTO.getDurationUnit() != null) {
            if (editPlannedPaymentDTO.getFrequency() == null) {
                throw new BadRequestException("Frequency and duration unit must be both set");
            }
            plannedPayment.setDurationUnit(editPlannedPaymentDTO.getDurationUnit());
        }
        if (editPlannedPaymentDTO.getAmount() != null) {
                ServiceCalculator.calculateBalance(plannedPayment.getAmount(), plannedPayment.getPaymentType(), plannedPayment.getAccount(), Action.REMOVE);
                plannedPayment.setAmount(editPlannedPaymentDTO.getAmount());
                ServiceCalculator.calculateBalance(plannedPayment.getAmount(), plannedPayment.getPaymentType(), plannedPayment.getAccount(), Action.ADD);
        }
        if (editPlannedPaymentDTO.getDueTime() != null) {
            if (editPlannedPaymentDTO.getDueTime().compareTo(new Timestamp(System.currentTimeMillis())) <= 0) {
                throw new BadRequestException("Invalid due time");
            }
            plannedPayment.setDueTime(editPlannedPaymentDTO.getDueTime());
        }
        if (editPlannedPaymentDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(editPlannedPaymentDTO.getCategoryId().intValue());
            Validator.validateCategory(category, userId);
            plannedPayment.getCategory().getPlannedPayments().remove(plannedPayment);
            plannedPayment.setPaymentType(category.getType());
            category.getPlannedPayments().add(plannedPayment);
            plannedPayment.setCategory(category);
        }
        plannedPaymentsRepository.save(plannedPayment);
        return plannedPayment;
    }

    public List<PlannedPayment> filter(int userId, FilterPlannedPaymentRequestDTO plannedPaymentRequestDTO) {
        return plannedPaymentDAO.filter(userId, plannedPaymentRequestDTO);
    }
}
