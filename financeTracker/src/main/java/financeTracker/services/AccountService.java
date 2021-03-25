package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public UserWithoutPassDTO createAcc(Account account, int ownerId) {
        Optional<User> optUser = userRepository.findById(ownerId);
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        if (accountRepository.findById(account.getId()).isPresent()) {
            throw new BadRequestException("Account already exists!");
        }
        User user = optUser.get();
        user.getAccounts().add(account);
        account.setUser(user);
        accountRepository.save(account);
        return new UserWithoutPassDTO(user);
    }

    public AccountWithoutOwnerDTO getById(int accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new BadRequestException("Account doesn't exist!");
        }
        return new AccountWithoutOwnerDTO(account.get());
    }
}
