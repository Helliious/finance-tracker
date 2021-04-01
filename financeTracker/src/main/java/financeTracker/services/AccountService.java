package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.AccountDAO;
import financeTracker.models.dto.account_dto.FilterAccountRequestDTO;
import financeTracker.models.dto.account_dto.UpdateRequestAccountDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public User createAcc(Account account, int ownerId) {
        account.setCreateTime(new Timestamp(System.currentTimeMillis()));
        Optional<User> optUser = userRepository.findById(ownerId);
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        for (Account a : optUser.get().getAccounts()) {
            if (a.getName().equals(account.getName()) || a.getId() == account.getId()) {
                throw new BadRequestException("Account already exists!");
            }
        }
        validateAccount(account);
        User user = optUser.get();
        user.getAccounts().add(account);
        account.setOwner(user);
        accountRepository.save(account);
        return user;
    }

    public Account getById(int accountId, int userId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account == null) {
            throw new NotFoundException("Account not found!");
        } else {
            return account;
        }
    }

    public List<Account> getAll(int userId) {
        return accountRepository.findAllByOwnerId(userId);
    }

    public Account deleteAccount(int accountId, int userId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account == null) {
            throw new NotFoundException("Account not found!");
        }
        accountRepository.deleteById(accountId);
        return account;
    }

    public Account editAccount(UpdateRequestAccountDTO accountDTO, int userId, int accountId) {
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
        if (accountDTO.getAccLimit() != null) {
            if (account.getAccLimit().equals(accountDTO.getAccLimit())) {
                throw new BadRequestException("Entered the same limit!");
            } else {
                account.setAccLimit(accountDTO.getAccLimit());
            }
        }
        if (accountDTO.getBalance() != null) {
            if (account.getBalance().equals(accountDTO.getBalance())) {
                throw new BadRequestException("Entered the same balance!");
            } else {
                account.setBalance(accountDTO.getBalance());
            }
        }
        accountRepository.save(account);
        return account;
    }

    private void validateAccount(Account account) {
        if (account.getName() == null) {
            throw new BadRequestException("Must enter valid account name!");
        }
        if (account.getBalance() == null || account.getBalance() < 0) {
            throw new BadRequestException("Must enter valid account balance!");
        }
        if (account.getAccLimit() < 0) {
            throw new BadRequestException("Must enter valid account limit!");
        }
    }

    public List<Account> filter(int userId, FilterAccountRequestDTO accountRequestDTO) {
        return accountDAO.filter(userId,accountRequestDTO);
    }
}
