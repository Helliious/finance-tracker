package financeTracker.services;

import financeTracker.exceptions.NotFoundException;
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
}
