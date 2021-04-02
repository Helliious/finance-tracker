package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.TransactionDAO;
import financeTracker.models.dto.transaction_dto.AddTransactionRequestDTO;
import financeTracker.models.dto.transaction_dto.EditTransactionRequestDTO;
import financeTracker.models.dto.transaction_dto.FilterTransactionRequestDTO;
import financeTracker.models.dto.transaction_dto.TransactionWithoutOwnerAndAccountDTO;
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

    public Transaction getById(int userId, int transactionId){
        Transaction transaction = transactionRepository.findByIdAndOwnerId(transactionId, userId);
        if (transaction == null) {
            throw new NotFoundException("Transaction not found!");
        }
        return transaction;
    }

    public List<Transaction> getByOwnerId(int ownerId) {
        return transactionRepository.findTransactionsByOwnerId(ownerId);
    }

    public List<Transaction> getByAccountId(int userId, int accountId) {
        Account account = accountRepository.findByIdAndOwnerId(accountId, userId);
        if (account == null) {
            throw new NotFoundException("Account not found!");
        }
        return account.getTransactions();
    }

    public TransactionWithoutOwnerAndAccountDTO delete(int transactionId, int userId) {
        Transaction transaction = transactionRepository.findByIdAndOwnerId(transactionId, userId);
        if (transaction == null) {
            throw new NotFoundException("Transaction not found!");
        }
        ServiceMethod.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.REMOVE);
        TransactionWithoutOwnerAndAccountDTO responseTransaction = new TransactionWithoutOwnerAndAccountDTO(transaction);
        transactionRepository.deleteById(transactionId);
        return responseTransaction;
    }

    public Transaction addTransactionToAccount(int accountId, AddTransactionRequestDTO dto, int ownerId) {
        dto.setCreateTime(new Timestamp(System.currentTimeMillis()));
        Account account = accountRepository.findByIdAndOwnerId(accountId, ownerId);
        Category category = categoryRepository.findByIdAndType(dto.getCategoryId(), dto.getType());
        Optional<User> optUser = userRepository.findById(ownerId);
        if (account == null){
            throw new NotFoundException("Account not found!");
        }
        if (category == null) {
            throw new NotFoundException("Category not found!");
        }
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        Transaction transaction = new Transaction(dto);
        User owner = optUser.get();
        transaction.setOwner(owner);
        transaction.setAccount(account);
        transaction.setCategory(category);
        owner.getTransactions().add(transaction);
        account.getTransactions().add(transaction);
        category.getTransactions().add(transaction);
        ServiceMethod.calculateBalance(transaction.getAmount(), transaction.getType(), account, Action.ADD);
        transactionRepository.save(transaction);
        return transaction;
    }

    public Transaction editTransaction(int transactionId, EditTransactionRequestDTO dto, int ownerId) {
        Transaction transaction = transactionRepository.findByIdAndOwnerId(transactionId, ownerId);
        if (transaction == null) {
            throw new NotFoundException("Transaction not found!");
        }
        if (dto.getType() != null) {
            ServiceMethod.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.REMOVE);
            transaction.setType(dto.getType());
            ServiceMethod.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.ADD);
        }
        if (dto.getAmount() != null) {
            ServiceMethod.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.REMOVE);
            transaction.setAmount(dto.getAmount());
            ServiceMethod.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.ADD);
        }
        if (dto.getAccountId() != null) {
            Account account = accountRepository.findByIdAndOwnerId(dto.getAccountId(), ownerId);
            if (account == null) {
                throw new NotFoundException("Account not found!");
            }
            ServiceMethod.calculateBalance(transaction.getAmount(), transaction.getType(), transaction.getAccount(), Action.REMOVE);
            transaction.getAccount().getTransactions().remove(transaction);
            transaction.setAccount(account);
            account.getTransactions().add(transaction);
            ServiceMethod.calculateBalance(transaction.getAmount(), transaction.getType(), account, Action.ADD);
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndOwnerId(dto.getCategoryId(), ownerId);
            if (category == null) {
                throw new NotFoundException("Category not found!");
            }
            transaction.getCategory().getTransactions().remove(transaction);
            transaction.setCategory(category);
            category.getTransactions().add(transaction);
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

    public TransactionWithoutOwnerAndAccountDTO addTransactionToBudget(int ownerId,int budgetId,int transactionId){
        Optional<Budget> optionalBudget = budgetRepository.findById(budgetId);
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
        if (optionalBudget.isEmpty()) {
            throw new NotFoundException("Budget doesn't exist");
        }
        if (optionalTransaction.isEmpty()) {
            throw new NotFoundException("Transaction doesn't exist");
        }
        Budget budget = optionalBudget.get();
        Transaction transaction = optionalTransaction.get();
        if (budget.getOwner().getId() != ownerId) {
            throw new NotFoundException("You don't own budget with such id");
        }
        if (transaction.getOwner().getId() != ownerId) {
            throw new NotFoundException("You don't own transaction with such id");
        }
        if (budget.getBudgetTransactions().contains(transaction)) {
            throw new BadRequestException("Transaction already exist in budget");
        }
        budget.getBudgetTransactions().add(transaction);
        budgetRepository.save(budget);
        return new TransactionWithoutOwnerAndAccountDTO(transactionRepository.findById(transactionId).get());
    }
}
