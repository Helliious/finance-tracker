package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.TransactionDAO;
import financeTracker.models.dto.transaction_dto.AddTransactionRequestDTO;
import financeTracker.models.dto.transaction_dto.EditTransactionRequestDTO;
import financeTracker.models.dto.transaction_dto.FilterTransactionRequestDTO;
import financeTracker.models.dto.transaction_dto.ResponseTransactionDTO;
import financeTracker.models.pojo.*;
import financeTracker.models.repository.*;
import financeTracker.utils.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionDAO transactionDAO;
    @Autowired
    private BudgetRepository budgetRepository;

    public Transaction getById(int userId, int transactionId, int accountId){
        Transaction transaction = transactionRepository.findByIdAndOwnerIdAndAccountId(transactionId, userId, accountId);
        if (transaction == null) {
            throw new NotFoundException("Transaction not found");
        }
        return transaction;
    }

    public List<Transaction> getByOwnerId(int ownerId) {
        return transactionRepository.findAllByOwnerId(ownerId);
    }

    public List<Transaction> getByAccountId(int userId, int accountId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account == null) {
            throw new NotFoundException("Account not found");
        }
        return account.getTransactions();
    }

    public ResponseTransactionDTO delete(int transactionId, int userId, int accountId) {
        Transaction transaction = transactionRepository.findByIdAndOwnerIdAndAccountId(transactionId, userId, accountId);
        if (transaction == null) {
            throw new NotFoundException("Transaction not found");
        }
        ServiceCalculator.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.REMOVE);
        ResponseTransactionDTO responseTransaction = new ResponseTransactionDTO(transaction);
        transactionRepository.deleteById(transactionId);
        return responseTransaction;
    }

    public Transaction addTransactionToAccount(int accountId, AddTransactionRequestDTO dto, int ownerId) {
        dto.setCreateTime(new Timestamp(System.currentTimeMillis()));
        Account account = accountRepository.findByIdAndOwnerId(accountId, ownerId);
        Category category = categoryRepository.findByIdAndOwnerId(dto.getCategoryId(), ownerId);
        Optional<User> optUser = userRepository.findById(ownerId);
        if (account == null){
            throw new NotFoundException("Account not found");
        }
        if (category == null) {
            category = categoryRepository.findByIdAndOwnerIsNull(dto.getCategoryId());
            if (category == null) {
                throw new NotFoundException("Category not found");
            }
        }
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        if (!category.getType().equals(dto.getType())) {
            throw new BadRequestException("Transaction type and category type must match");
        }
        Transaction transaction = new Transaction(dto);
        User owner = optUser.get();
        transaction.setOwner(owner);
        transaction.setAccount(account);
        transaction.setCategory(category);
        owner.getTransactions().add(transaction);
        account.getTransactions().add(transaction);
        category.getTransactions().add(transaction);
        ServiceCalculator.calculateBalance(transaction.getAmount(), transaction.getType(), account, Action.ADD);
        transactionRepository.save(transaction);
        return transaction;
    }

    public Transaction editTransaction(int transactionId, EditTransactionRequestDTO dto, int ownerId, int accountId) {
        Transaction transaction = transactionRepository.findByIdAndOwnerIdAndAccountId(transactionId, ownerId, accountId);
        if (transaction == null) {
            throw new NotFoundException("Transaction not found");
        }
        if (dto.getAmount() != null) {
            ServiceCalculator.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.REMOVE);
            transaction.setAmount(dto.getAmount());
            ServiceCalculator.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.ADD);
        }
        if (dto.getAccountId() != null) {
            Account account = accountRepository.findByIdAndOwnerId(dto.getAccountId(), ownerId);
            if (account == null) {
                throw new NotFoundException("Account not found");
            }
            ServiceCalculator.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.REMOVE);
            transaction.getAccount().getTransactions().remove(transaction);
            transaction.setAccount(account);
            account.getTransactions().add(transaction);
            ServiceCalculator.calculateBalance(transaction.getAmount(), transaction.getType(), account, Action.ADD);
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndOwnerId(dto.getCategoryId(), ownerId);
            if (category == null) {
                category = categoryRepository.findByIdAndOwnerIsNull(dto.getCategoryId());
                if (category == null) {
                    throw new NotFoundException("Category not found!");
                }
            }
            ServiceCalculator.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.REMOVE);
            transaction.getCategory().getTransactions().remove(transaction);
            transaction.setType(category.getType());
            transaction.setCategory(category);
            category.getTransactions().add(transaction);
            ServiceCalculator.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.ADD);
        }
        if (dto.getDescription() != null) {
            transaction.setDescription(dto.getDescription());
        }
        transactionRepository.save(transaction);
        return transaction;
    }

    public List<Transaction> filter(int userId, FilterTransactionRequestDTO dto) {
        return transactionDAO.filterTransaction(userId, dto);
    }

    public Transaction addTransactionToBudget(int ownerId,
                                              int accountId,
                                              int budgetId,
                                              int transactionId){
        Transaction transaction = transactionRepository.findByIdAndOwnerIdAndAccountId(transactionId, ownerId, accountId);
        if (transaction == null) {
            throw new NotFoundException("Transaction not found!");
        }
        Budget budget = budgetRepository.findByIdAndOwnerIdAndAccountId(budgetId, ownerId, accountId);
        if (budget == null) {
            throw new NotFoundException("Budget not found!");
        }
        Optional<User> optUser = userRepository.findById(ownerId);
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        Category category = categoryRepository.findByIdAndOwnerId(transaction.getCategory().getId(), ownerId);
        if (category == null) {
            category = categoryRepository.findByIdAndOwnerIsNull(transaction.getCategory().getId());
            if (category == null) {
                throw new NotFoundException("Category not found!");
            }
        }
        if (budget.getBudgetTransactions().contains(transaction)) {
            throw new BadRequestException("Transaction already exist in budget");
        }
        budget.getBudgetTransactions().add(transaction);
        transaction.getBudgetsThatHaveTransaction().add(budget);
        budgetRepository.save(budget);
        return transaction;
    }
}
