package financeTracker.services;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.account_dto.UpdateRequestAccountDTO;
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
        for (Account a : optUser.get().getAccounts()) {
            if (a.getName().equals(account.getName()) || a.getId() == account.getId()) {
                throw new BadRequestException("Account already exists!");
            }
        }
        User user = optUser.get();
        user.getAccounts().add(account);
        account.setOwner(user);
        accountRepository.save(account);
        return new UserWithoutPassDTO(user);
    }

    public AccountWithoutOwnerDTO getById(int accountId, int userId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account == null) {
            throw new NotFoundException("Account not found!");
        } else {
            return new AccountWithoutOwnerDTO(account);
        }
    }

    public List<AccountWithoutOwnerDTO> getAll(int userId) {
        List<Account> accounts = accountRepository.findAllByOwnerId(userId);
        if (accounts.isEmpty()) {
            throw new NotFoundException("Accounts not found!");
        }
        List<AccountWithoutOwnerDTO> response = new ArrayList<>();
        for (Account a : accounts) {
            response.add(new AccountWithoutOwnerDTO(a));
        }
        return response;
    }

    public AccountWithoutOwnerDTO deleteAccount(int accountId, int userId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account == null) {
            throw new NotFoundException("Account not found!");
        }
        AccountWithoutOwnerDTO responseAcc = new AccountWithoutOwnerDTO(account);
        accountRepository.deleteById(accountId);
        return responseAcc;
    }

    public UserWithoutPassDTO editAccount(UpdateRequestAccountDTO accountDTO, int userId, int accountId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account == null) {
            throw new NotFoundException("Account not found!");
        }
        if (accountDTO.getName() != null) {
            if (account.getName().equals(accountDTO.getName())) {
                throw new BadRequestException("New account has the same name!");
            } else if (accountRepository.findAccountByNameAndOwnerId(accountDTO.getName(), userId) != null) {
                throw new BadRequestException("Account name already exists!");
            } else {
                account.setName(accountDTO.getName());
            }
        }
        if (accountDTO.getAccLimit() > 0) {
            if (account.getAccLimit() == accountDTO.getAccLimit()) {
                throw new BadRequestException("Entered the same limit!");
            } else {
                account.setAccLimit(accountDTO.getAccLimit());
            }
        }
        if (accountDTO.getBalance() > 0) {
            if (account.getBalance() == accountDTO.getBalance()) {
                throw new BadRequestException("Entered the same balance!");
            } else {
                account.setBalance(accountDTO.getBalance());
            }
        }
        accountRepository.save(account);
        UserWithoutPassDTO responseUser = new UserWithoutPassDTO(account.getOwner());
        return responseUser;
    }
}
