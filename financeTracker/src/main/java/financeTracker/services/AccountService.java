package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.AccountDAO;
import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.account_dto.CreateAccountDTO;
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

    public Account createAcc(CreateAccountDTO createAccountDTO, int ownerId) {
        if (createAccountDTO.getAccLimit() != null &&
                createAccountDTO.getBalance() > createAccountDTO.getAccLimit()) {
            throw new BadRequestException("Balance cannot be bigger than account limit");
        }
        createAccountDTO.setCreateTime(new Timestamp(System.currentTimeMillis()));
        Optional<User> optUser = userRepository.findById(ownerId);
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        User user = optUser.get();
        for (Account a : user.getAccounts()) {
            if (a.getName().equals(createAccountDTO.getName())) {
                throw new BadRequestException("Account already exists!");
            }
        }
        Account account = new Account(createAccountDTO);
        account.setOwner(user);
        user.getAccounts().add(account);
        return accountRepository.save(account);
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

    public AccountWithoutOwnerDTO deleteAccount(int accountId, int userId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account == null) {
            throw new NotFoundException("Account not found!");
        }
        //cannot map entity after it is deleted
        AccountWithoutOwnerDTO resultAccount = new AccountWithoutOwnerDTO(account);
        accountRepository.deleteById(accountId);
        return resultAccount;
    }

    public Account editAccount(UpdateRequestAccountDTO accountDTO, int userId, int accountId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account == null) {
            throw new NotFoundException("Account not found!");
        }
        if (accountDTO.getName() != null) {
            if (accountRepository.findAccountByNameAndOwnerId(accountDTO.getName(), userId) != null) {
                throw new BadRequestException("Account name already exists!");
            }
            account.setName(accountDTO.getName());
        }
        if (accountDTO.getAccLimit() == null) {
            if (account.getAccLimit() != null) {
                account.setAccLimit(null);
            }
        }
        if (accountDTO.getAccLimit() != null) {
            if (account.getAccLimit() != null && account.getBalance() > accountDTO.getAccLimit()) {
                throw new BadRequestException("Balance cannot be bigger than account limit");
            }
            account.setAccLimit(accountDTO.getAccLimit());
        }
        if (accountDTO.getBalance() != null) {
            if (account.getAccLimit() != null && account.getAccLimit() < accountDTO.getBalance()) {
                throw new BadRequestException("Balance cannot be bigger than account limit");
            }
            account.setBalance(accountDTO.getBalance());
        }
        accountRepository.save(account);
        return account;
    }

    public List<Account> filter(int userId, FilterAccountRequestDTO accountRequestDTO) {
        return accountDAO.filter(userId,accountRequestDTO);
    }
}
