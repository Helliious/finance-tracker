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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
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

    public TransactionWithoutOwnerAndAccountDTO getById(int userId, int id){
        Optional<Transaction> optionalTransaction=transactionRepository.findById(id);

        if (optionalTransaction.isEmpty()){
            throw new NotFoundException("Transaction not found");
        }
        Transaction transaction=optionalTransaction.get();
        if(transaction.getOwner().getId()!=userId){
            throw new BadRequestException("You can't see other users transaction");
        }
        return new TransactionWithoutOwnerAndAccountDTO(transaction);
    }

    public ArrayList<TransactionWithoutOwnerAndAccountDTO> getByOwnerId(int id) {
        ArrayList<Transaction> transactions=transactionRepository.findTransactionsByOwner_Id(id);
        if (transactions.isEmpty()){
            throw new NotFoundException("User don't have transactions");
        }
        ArrayList<TransactionWithoutOwnerAndAccountDTO> responseTransactions =new ArrayList<>();
        for (Transaction transaction:transactions){
            TransactionWithoutOwnerAndAccountDTO responseTransaction=new TransactionWithoutOwnerAndAccountDTO(transaction);
            responseTransactions.add(responseTransaction);
        }
        return responseTransactions;
    }

    public ArrayList<TransactionWithoutOwnerAndAccountDTO> getByAccountId(int userId,int accountId) {
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if (optionalAccount.isEmpty()){
            throw new NotFoundException("Account does not exist..");
        }
        Account account=optionalAccount.get();
        if(account.getOwner().getId()!=userId){
            throw new BadRequestException("You can't see account transactions of account that you are not owner!!");
        }
        ArrayList<Transaction> transactions=transactionRepository.findTransactionsByAccount_Id(accountId);
        if (transactions.isEmpty()){
            throw new NotFoundException("This account dont'have transactions");
        }
        ArrayList<TransactionWithoutOwnerAndAccountDTO> responseTransactions=new ArrayList<>();
        for (Transaction transaction:transactions){
            TransactionWithoutOwnerAndAccountDTO responseTransaction=new TransactionWithoutOwnerAndAccountDTO(transaction);
            responseTransactions.add(responseTransaction);
        }
        return responseTransactions;
    }

    public TransactionWithoutOwnerAndAccountDTO delete(int id, int userId) {
        Optional<Transaction> optionalTransaction=transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()){
            throw new  NotFoundException("Transaction doesn't exist");
        }
        Transaction transaction=optionalTransaction.get();
        if(transaction.getId()!=userId){
            throw new BadRequestException("You can't delete this transaction, because you are not the owner!!");
        }
        TransactionWithoutOwnerAndAccountDTO responseTransaction=new TransactionWithoutOwnerAndAccountDTO(transaction);
        transactionRepository.deleteById(id);
        return responseTransaction;
    }

    public TransactionWithoutOwnerAndAccountDTO addTransactionToAcc(int accountId, AddTransactionRequestDTO dto, int ownerId) {
        Optional<Account> optionalAccount= accountRepository.findById(accountId);
        Optional<User> optionalUser=userRepository.findById(ownerId);
        Optional<Category> optionalCategory=categoryRepository.findById(dto.getCategoryId());
        if (optionalAccount.isEmpty()){
            throw new  NotFoundException("Account doesn't exist");
        }
        if (optionalCategory.isEmpty()){
            throw new NotFoundException("Category doesn't exist");
        }
        Transaction transaction=new Transaction(dto);
        Account account=optionalAccount.get();
        if(account.getOwner().getId()!=ownerId){
            throw new NotFoundException("You don't own account with such id");
        }
        User owner=optionalUser.get();
        Category category=optionalCategory.get();
        transaction.setType(dto.getType());
        transaction.setDescription(dto.getDescription());
        transaction.setAmount(dto.getAmount());
        transaction.setOwner(owner);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transactionRepository.save(transaction);
        return new TransactionWithoutOwnerAndAccountDTO(transaction);
    }

    public TransactionWithoutOwnerAndAccountDTO editTransaction(int transactionID, EditTransactionRequestDTO dto, int ownerId) {
        Optional<Transaction> optionalTransaction=transactionRepository.findById(transactionID);
        Optional<Account> optionalAccount=accountRepository.findById(dto.getAccountId());
        Optional<Category> optionalCategory=categoryRepository.findById(dto.getCategoryId());
        if (optionalTransaction.isEmpty()){
            throw new NotFoundException("Transaction doesn't exist");
        }
        if (optionalAccount.isEmpty()){
            throw new NotFoundException("Account doesn't exist");
        }
        if (optionalCategory.isEmpty()){
            throw new NotFoundException("Category doesn't exist");
        }
        Transaction transaction= optionalTransaction.get();
        Account account=optionalAccount.get();
        Category category=optionalCategory.get();
        if (account.getOwner().getId()!=ownerId){
            throw new BadRequestException("You can't set transaction to account that you aren't owner");
        }
        if (dto.getType()!=null){
            transaction.setType(dto.getType());
        }
        if (dto.getAmount()>0){
            transaction.setAmount(dto.getAmount());
        }
        if (dto.getDescription()!=null){
            transaction.setDescription(dto.getDescription());
        }

        transaction.setCategory(category);
        transaction.setAccount(account);
        transactionRepository.save(transaction);
        return new TransactionWithoutOwnerAndAccountDTO(transaction);
    }

    public List<TransactionWithoutOwnerAndAccountDTO> filter(int userId, FilterTransactionRequestDTO dto) {
        List<Transaction> transactions=transactionDAO.filterTransaction(userId,dto);
        List<TransactionWithoutOwnerAndAccountDTO> transactionWithoutOwnerAndAccountDTOS=new ArrayList<>();
        for (Transaction transaction:transactions){
            transactionWithoutOwnerAndAccountDTOS.add(new TransactionWithoutOwnerAndAccountDTO(transaction));
        }
        return transactionWithoutOwnerAndAccountDTOS;
    }
    public TransactionWithoutOwnerAndAccountDTO addTransactionToBudget(int ownerId,int budgetId,int transactionId){
        Optional<Budget> optionalBudget=budgetRepository.findById(budgetId);
        Optional<Transaction> optionalTransaction=transactionRepository.findById(transactionId);
        if (optionalBudget.isEmpty()){
            throw new NotFoundException("Budget doesn't exist");
        }
        if (optionalTransaction.isEmpty()){
            throw new NotFoundException("Transaction doesn't exist");
        }
        Budget budget =optionalBudget.get();
        Transaction transaction=optionalTransaction.get();
        if(budget.getOwner().getId()!=ownerId){
            throw new NotFoundException("You don't own budget with such id");
        }
        if (transaction.getOwner().getId()!=ownerId){
            throw new NotFoundException("You don't own transaction with such id");
        }
        if (budget.getBudgetTransactions().contains(transaction)){
            throw new BadRequestException("Transaction already exist in budget");
        }
            budget.getBudgetTransactions().add(transaction);
            budgetRepository.save(budget);

        return new TransactionWithoutOwnerAndAccountDTO(transactionRepository.findById(transactionId).get());
    }

}
