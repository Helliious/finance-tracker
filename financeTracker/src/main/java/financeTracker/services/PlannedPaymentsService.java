package financeTracker.services;

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
        if (optUser.isEmpty() || optAccount.isEmpty() || optCategory.isEmpty()) {
            throw new NotFoundException("User/Account/Category not found!");
        }
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

    public ResponsePlannedPaymentDTO getById(int accountId, int plannedPaymentId) {
        boolean isPresent = false;
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new NotFoundException("Account not found!");
        }
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

    public List<ResponsePlannedPaymentDTO> getAll(int accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new NotFoundException("Account not found!");
        }
        List<ResponsePlannedPaymentDTO> plannedPayments = new ArrayList<>();
        for (PlannedPayment p : account.get().getPlannedPayments()) {
            plannedPayments.add(new ResponsePlannedPaymentDTO(p));
        }
        return plannedPayments;
    }

    public ResponsePlannedPaymentDTO delete(int accountId, int plannedPaymentId) {
        PlannedPayment plannedPayment = plannedPaymentsRepository.findPlannedPaymentByIdAndAccount_Id(plannedPaymentId, accountId);
        if (plannedPayment == null) {
            throw new NotFoundException("Planned payment not found!");
        }
        ResponsePlannedPaymentDTO responsePlannedPaymentDTO = new ResponsePlannedPaymentDTO(plannedPayment);
        plannedPaymentsRepository.deleteById(plannedPaymentId);
        return responsePlannedPaymentDTO;
    }

    public UserWithoutPassDTO edit(ResponsePlannedPaymentDTO responsePlannedPaymentDTO, int accountId, int plannedPaymentId) {
        Optional<Account> account = accountRepository.findById(accountId);
        Optional<PlannedPayment> plannedPayment = plannedPaymentsRepository.findById(plannedPaymentId);
        if (account.isEmpty() || plannedPayment.isEmpty()) {
            throw new NotFoundException("Account/Planned payment not found!");
        }
        if (responsePlannedPaymentDTO.getName() != null) {
            if (plannedPaymentsRepository.findPlannedPaymentByNameAndAccount(responsePlannedPaymentDTO.getName(), account.get()) != null) {
                throw new BadRequestException("Planned payment name already exists!");
            } else {
                plannedPayment.get().setName(responsePlannedPaymentDTO.getName());
            }
        }
        if (responsePlannedPaymentDTO.getPaymentType() != null) {
            if (plannedPayment.get().getPaymentType().equals(responsePlannedPaymentDTO.getPaymentType())) {
                throw new BadRequestException("Entered the same type!");
            } else {
                plannedPayment.get().setPaymentType(responsePlannedPaymentDTO.getPaymentType());
            }
        }
        if (responsePlannedPaymentDTO.getFrequency() != 0) {
            if (plannedPayment.get().getFrequency() == responsePlannedPaymentDTO.getFrequency()) {
                throw new BadRequestException("Entered the same frequency!");
            } else {
                plannedPayment.get().setFrequency(responsePlannedPaymentDTO.getFrequency());
            }
        }
        if (responsePlannedPaymentDTO.getDurationUnit() != null) {
            if (plannedPayment.get().getDurationUnit().equals(responsePlannedPaymentDTO.getDurationUnit())) {
                throw new BadRequestException("Entered the same duration unit!");
            } else {
                plannedPayment.get().setDurationUnit(responsePlannedPaymentDTO.getDurationUnit());
            }
        }
        if (responsePlannedPaymentDTO.getAmount() != 0) {
            if (plannedPayment.get().getAmount() == responsePlannedPaymentDTO.getAmount()) {
                throw new BadRequestException("Entered the same amount!");
            } else {
                plannedPayment.get().setAmount(responsePlannedPaymentDTO.getAmount());
            }
        }
        if (responsePlannedPaymentDTO.getDueTime() != null) {
            if (plannedPayment.get().getDueTime() == responsePlannedPaymentDTO.getDueTime()) {
                throw new BadRequestException("Entered the same time!");
            } else {
                plannedPayment.get().setDueTime(responsePlannedPaymentDTO.getDueTime());
            }
        }
        if (responsePlannedPaymentDTO.getCategory() != null) {
            if (plannedPayment.get().getCategory().getName().equals(responsePlannedPaymentDTO.getCategory().getName())) {
                throw new BadRequestException("Entered the same category!");
            } else {
                Optional<Category> category = categoryRepository.findById(responsePlannedPaymentDTO.getCategory().getId());
                if (category.isEmpty()) {
                    throw new NotFoundException("Category not found!");
                }
                plannedPayment.get().setCategory(category.get());
            }
        }
        plannedPaymentsRepository.save(plannedPayment.get());
        User user = account.get().getOwner();
        return new UserWithoutPassDTO(user);
    }
}
