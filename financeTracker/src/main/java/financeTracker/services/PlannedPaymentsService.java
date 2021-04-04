package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.PlannedPaymentDAO;
import financeTracker.models.dto.planned_payment_dto.AddPlannedPaymentDTO;
import financeTracker.models.dto.planned_payment_dto.EditPlannedPaymentDTO;
import financeTracker.models.dto.planned_payment_dto.FilterPlannedPaymentRequestDTO;
import financeTracker.models.dto.planned_payment_dto.ResponsePlannedPaymentDTO;
import financeTracker.models.pojo.*;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.PlannedPaymentsRepository;
import financeTracker.models.repository.UserRepository;
import financeTracker.utils.Action;
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
        Category category = categoryRepository.findByIdAndOwnerId(plannedPaymentDTO.getCategoryId(), userId);
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        if (account == null) {
            throw new NotFoundException("Account not found");
        }
        if (category == null) {
            category = categoryRepository.findByIdAndOwnerIsNull(plannedPaymentDTO.getCategoryId());
            if (category == null) {
                throw new NotFoundException("Category not found");
            }
        }
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
            } else if (plannedPayment.getName().equals(editPlannedPaymentDTO.getName())) {
                throw new BadRequestException("Entered the same planned payment name");
            } else {
                plannedPayment.setName(editPlannedPaymentDTO.getName());
            }
        }
        if (editPlannedPaymentDTO.getFrequency() != null) {
            if (editPlannedPaymentDTO.getDurationUnit() == null) {
                throw new BadRequestException("Frequency and duration unit must be both set");
            }
            if (plannedPayment.getFrequency() == editPlannedPaymentDTO.getFrequency()) {
                throw new BadRequestException("Entered the same frequency");
            } else {
                plannedPayment.setFrequency(editPlannedPaymentDTO.getFrequency());
            }
        }
        if (editPlannedPaymentDTO.getDurationUnit() != null) {
            if (editPlannedPaymentDTO.getFrequency() == null) {
                throw new BadRequestException("Frequency and duration unit must be both set");
            }
            if (plannedPayment.getDurationUnit().equals(editPlannedPaymentDTO.getDurationUnit())) {
                throw new BadRequestException("Entered the same duration unit");
            } else {
                plannedPayment.setDurationUnit(editPlannedPaymentDTO.getDurationUnit());
            }
        }
        if (editPlannedPaymentDTO.getAmount() != null) {
            if (plannedPayment.getAmount() == editPlannedPaymentDTO.getAmount()) {
                throw new BadRequestException("Entered the same amount!");
            } else {
                ServiceCalculator.calculateBalance(plannedPayment.getAmount(), plannedPayment.getPaymentType(), plannedPayment.getAccount(), Action.REMOVE);
                plannedPayment.setAmount(editPlannedPaymentDTO.getAmount());
                ServiceCalculator.calculateBalance(plannedPayment.getAmount(), plannedPayment.getPaymentType(), plannedPayment.getAccount(), Action.ADD);
            }
        }
        if (editPlannedPaymentDTO.getDueTime() != null) {
            if (plannedPayment.getDueTime() == editPlannedPaymentDTO.getDueTime()) {
                throw new BadRequestException("Entered the same time");
            } else if (editPlannedPaymentDTO.getDueTime().compareTo(new Timestamp(System.currentTimeMillis())) <= 0) {
                throw new BadRequestException("Invalid due time");
            } else {
                plannedPayment.setDueTime(editPlannedPaymentDTO.getDueTime());
            }
        }
        if (editPlannedPaymentDTO.getCategory() != null) {
            if (plannedPayment.getCategory().getName().equals(editPlannedPaymentDTO.getCategory().getName())) {
                throw new BadRequestException("Entered the same category");
            } else {
                Category category = categoryRepository.findByIdAndOwnerId(
                        editPlannedPaymentDTO.getCategory().getId(),
                        userId
                );
                if (category == null) {
                    throw new NotFoundException("Category not found!");
                }
                plannedPayment.getCategory().getPlannedPayments().remove(plannedPayment);
                plannedPayment.setPaymentType(category.getType());
                category.getPlannedPayments().add(plannedPayment);
                plannedPayment.setCategory(category);
            }
        }
        plannedPaymentsRepository.save(plannedPayment);
        return plannedPayment;
    }

    public List<PlannedPayment> filter(int userId, FilterPlannedPaymentRequestDTO plannedPaymentRequestDTO) {
        return plannedPaymentDAO.filter(userId, plannedPaymentRequestDTO);
    }
}
