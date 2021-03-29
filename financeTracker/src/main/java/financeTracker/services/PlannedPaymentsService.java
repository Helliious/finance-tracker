package financeTracker.services;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.planned_payment_dto.ResponsePlannedPaymentDTO;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Category;
import financeTracker.models.pojo.PlannedPayment;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.PlannedPaymentsRepository;
import financeTracker.models.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlannedPaymentsService {
    @Autowired
    private PlannedPaymentsRepository plannedPaymentsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public UserWithoutPassDTO add(PlannedPayment plannedPayment, int userId, int accountId) {
        Optional<User> optUser = userRepository.findById(userId);
        Optional<Account> optAccount = accountRepository.findById(accountId);
        Optional<Category> optCategory = categoryRepository.findById(plannedPayment.getCategory().getId());
        //TODO: split if statement to 3 ifs
        if (optUser.isEmpty() || optAccount.isEmpty() || optCategory.isEmpty()) {
            throw new NotFoundException("User/Account/Category not found!");
        }
        //TODO: validate user_id, account_id, planned_payment owner
        User user = optUser.get();
        Account account = optAccount.get();
        Category category = optCategory.get();
        user.getPlannedPayments().add(plannedPayment);
        category.getPlannedPayments().add(plannedPayment);
        account.getPlannedPayments().add(plannedPayment);
        plannedPayment.setOwner(user);
        plannedPayment.setAccount(account);
        plannedPayment.setCategory(category);
        plannedPaymentsRepository.save(plannedPayment);
        return new UserWithoutPassDTO(user);
    }

    public ResponsePlannedPaymentDTO getById(int accountId, int userId, int plannedPaymentId) {
        boolean isPresent = false;
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new NotFoundException("Account not found!");
        }
        //TODO: validate user_id, planned_payment account_id
        Optional<PlannedPayment> plannedPayment = plannedPaymentsRepository.findById(plannedPaymentId);
        if (plannedPayment.isEmpty()) {
            throw new NotFoundException("Planned payment not found!");
        }
        for (PlannedPayment p : account.get().getPlannedPayments()) {
            if (p.getId() == plannedPayment.get().getId()) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            throw new NotFoundException("Planned payment not found in this account!");
        }
        return new ResponsePlannedPaymentDTO(plannedPayment.get());
    }

    public List<ResponsePlannedPaymentDTO> getAll(int accountId, int userId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new NotFoundException("Account not found!");
        }
        if (account.get().getOwner().getId() != userId) {
            throw new AuthenticationException("Cannot see planned payments of other users!");
        }
        List<ResponsePlannedPaymentDTO> plannedPayments = new ArrayList<>();
        for (PlannedPayment p : account.get().getPlannedPayments()) {
            plannedPayments.add(new ResponsePlannedPaymentDTO(p));
        }
        return plannedPayments;
    }

    public ResponsePlannedPaymentDTO delete(int accountId, int userId, int plannedPaymentId) {
        PlannedPayment plannedPayment = plannedPaymentsRepository.findPlannedPaymentByIdAndAccountIdAAndOwnerId(plannedPaymentId, accountId, userId);
        if (plannedPayment == null) {
            throw new NotFoundException("Planned payment not found!");
        }
        ResponsePlannedPaymentDTO responsePlannedPaymentDTO = new ResponsePlannedPaymentDTO(plannedPayment);
        plannedPaymentsRepository.deleteById(plannedPaymentId);
        return responsePlannedPaymentDTO;
    }

    public ResponsePlannedPaymentDTO edit(ResponsePlannedPaymentDTO responsePlannedPaymentDTO, int accountId, int userId, int plannedPaymentId) {
        PlannedPayment plannedPayment = plannedPaymentsRepository.findPlannedPaymentByIdAndAccountIdAAndOwnerId(plannedPaymentId, accountId, userId);
        if (plannedPayment == null) {
            throw new NotFoundException("Planned payment not found!");
        }
        if (responsePlannedPaymentDTO.getName() != null) {
            if (plannedPaymentsRepository.findPlannedPaymentByNameAndAccountId(responsePlannedPaymentDTO.getName(), accountId) != null) {
                throw new BadRequestException("Planned payment name already exists!");
            } else {
                plannedPayment.setName(responsePlannedPaymentDTO.getName());
            }
        }
        if (responsePlannedPaymentDTO.getPaymentType() != null) {
            if (plannedPayment.getPaymentType().equals(responsePlannedPaymentDTO.getPaymentType())) {
                throw new BadRequestException("Entered the same type!");
            } else {
                plannedPayment.setPaymentType(responsePlannedPaymentDTO.getPaymentType());
            }
        }
        if (responsePlannedPaymentDTO.getFrequency() != 0) {
            if (plannedPayment.getFrequency() == responsePlannedPaymentDTO.getFrequency()) {
                throw new BadRequestException("Entered the same frequency!");
            } else {
                plannedPayment.setFrequency(responsePlannedPaymentDTO.getFrequency());
            }
        }
        if (responsePlannedPaymentDTO.getDurationUnit() != null) {
            if (plannedPayment.getDurationUnit().equals(responsePlannedPaymentDTO.getDurationUnit())) {
                throw new BadRequestException("Entered the same duration unit!");
            } else {
                plannedPayment.setDurationUnit(responsePlannedPaymentDTO.getDurationUnit());
            }
        }
        if (responsePlannedPaymentDTO.getAmount() != 0) {
            if (plannedPayment.getAmount() == responsePlannedPaymentDTO.getAmount()) {
                throw new BadRequestException("Entered the same amount!");
            } else {
                plannedPayment.setAmount(responsePlannedPaymentDTO.getAmount());
            }
        }
        if (responsePlannedPaymentDTO.getDueTime() != null) {
            if (plannedPayment.getDueTime() == responsePlannedPaymentDTO.getDueTime()) {
                throw new BadRequestException("Entered the same time!");
            } else {
                plannedPayment.setDueTime(responsePlannedPaymentDTO.getDueTime());
            }
        }
        if (responsePlannedPaymentDTO.getCategory() != null) {
            if (plannedPayment.getCategory().getName().equals(responsePlannedPaymentDTO.getCategory().getName())) {
                throw new BadRequestException("Entered the same category!");
            } else {
                Optional<Category> category = categoryRepository.findById(responsePlannedPaymentDTO.getCategory().getId());
                if (category.isEmpty()) {
                    throw new NotFoundException("Category not found!");
                }
                plannedPayment.setCategory(category.get());
            }
        }
        plannedPaymentsRepository.save(plannedPayment);
        return new ResponsePlannedPaymentDTO(plannedPayment);
    }
}
