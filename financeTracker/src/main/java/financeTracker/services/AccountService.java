package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.account_dto.UpdateRequestAccountDTO;
import financeTracker.models.dto.user_dto.UpdateRequestUserDTO;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
            throw new NotFoundException("Account not found!");
        }
        return new AccountWithoutOwnerDTO(account.get());
    }

    public List<AccountWithoutOwnerDTO> getAll(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        List<AccountWithoutOwnerDTO> accounts = new ArrayList<>();
        for (Account a : user.get().getAccounts()) {
            accounts.add(new AccountWithoutOwnerDTO(a));
        }
        return accounts;
    }

    public AccountWithoutOwnerDTO deleteAccount(int accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new NotFoundException("Account not found!");
        }
        AccountWithoutOwnerDTO responseAcc = new AccountWithoutOwnerDTO(account.get());
        accountRepository.deleteById(accountId);
        return responseAcc;
    }

    public UserWithoutPassDTO editAccount(UpdateRequestAccountDTO accountDTO, int userId, int accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        Optional<User> user = userRepository.findById(userId);
        if (account.isEmpty() || user.isEmpty()) {
            throw new NotFoundException("User/Account not found!");
        }
        if (accountDTO.getName() != null) {
            if (accountRepository.findAccountByNameAndUser(accountDTO.getName(), user.get()) != null) {
                throw new BadRequestException("Account name already exists!");
            } else {
                account.get().setName(accountDTO.getName());
            }
        }
        if (accountDTO.getAccLimit() != 0) {
            if (account.get().getAccLimit() == accountDTO.getAccLimit()) {
                throw new BadRequestException("Entered the same limit!");
            } else {
                account.get().setAccLimit(accountDTO.getAccLimit());
            }
        }
        if (accountDTO.getBalance() != 0) {
            if (account.get().getBalance() == accountDTO.getBalance()) {
                throw new BadRequestException("Entered the same balance!");
            } else {
                account.get().setBalance(accountDTO.getBalance());
            }
        }
        accountRepository.save(account.get());
        user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        UserWithoutPassDTO responseUser = new UserWithoutPassDTO(user.get());
        return responseUser;
    }
}
