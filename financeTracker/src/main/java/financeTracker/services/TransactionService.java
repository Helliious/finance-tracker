package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.transaction_dto.AddTransactionRequestDTO;
import financeTracker.models.dto.transaction_dto.EditTransactionRequestDTO;
import financeTracker.models.dto.transaction_dto.TransactionWithoutOwnerDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Category;
import financeTracker.models.pojo.Transaction;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.TransactionRepository;
import financeTracker.models.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
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

    public TransactionWithoutOwnerDTO getById(int id){
        Optional<Transaction> optionalTransaction=transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()){
            throw new NotFoundException("Transaction not found");
        }
        else{
            return new TransactionWithoutOwnerDTO(optionalTransaction.get());
        }
    }
    public ArrayList<TransactionWithoutOwnerDTO> getByOwnerId(int id){
         ArrayList<Transaction> transactions=transactionRepository.findTransactionsByOwner_Id(id);
         if (transactions.isEmpty()){
             throw new NotFoundException("User don't have transactions");
         }
        ArrayList<TransactionWithoutOwnerDTO> responseTransactions =new ArrayList<>();
        for (Transaction transaction:transactions){
            TransactionWithoutOwnerDTO responseTransaction=new TransactionWithoutOwnerDTO(transaction);
            responseTransactions.add(responseTransaction);
        }
        return responseTransactions;
    }
    public ArrayList<TransactionWithoutOwnerDTO> getByAccountId(int id){
        ArrayList<Transaction> transactions=transactionRepository.findTransactionsByAccount_Id(id);
        if (transactions.isEmpty()){
            throw new NotFoundException("This account dont'have transactions");
        }
        ArrayList<TransactionWithoutOwnerDTO> responseTransactions=new ArrayList<>();
        for (Transaction transaction:transactions){
            TransactionWithoutOwnerDTO responseTransaction=new TransactionWithoutOwnerDTO(transaction);
            responseTransactions.add(responseTransaction);
        }
        return responseTransactions;
    }
    public TransactionWithoutOwnerDTO delete(int id, int userId){
        Optional<Transaction> optionalTransaction=transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()){
            throw new  NotFoundException("Transaction doesn't exist");
        }
        Transaction transaction=optionalTransaction.get();
        if(transaction.getId()!=userId){
            throw new NotFoundException("You don't own transaction with such id");
        }
        TransactionWithoutOwnerDTO responseTransaction=new TransactionWithoutOwnerDTO(transaction);
        transactionRepository.deleteById(id);
        return responseTransaction;
    }
    public TransactionWithoutOwnerDTO addTransactionToAcc(int accountId,AddTransactionRequestDTO dto){
        Optional<Account> optionalAccount= accountRepository.findById(accountId);
        Optional<User> optionalUser=userRepository.findById(dto.getUserId());
        Optional<Category> optionalCategory=categoryRepository.findById(dto.getCategoryId());
        if (optionalAccount.isEmpty()){
            throw new  NotFoundException("Account doesn't exist");
        }
        if (optionalUser.isEmpty()){
            throw new NotFoundException("User doesn't exist");
        }
        if (optionalCategory.isEmpty()){
            throw new NotFoundException("Category doesn't exist");
        }
        Transaction transaction=new Transaction(dto);
        Account account=optionalAccount.get();
        //validation that check is account from dto owner is current user
        if(account.getOwner().getId()!=dto.getUserId()){
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
        return new TransactionWithoutOwnerDTO(transaction);
    }
    public TransactionWithoutOwnerDTO editTransaction(int transactionID, EditTransactionRequestDTO dto,int ownerId){
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
        return new TransactionWithoutOwnerDTO(transaction);
    }

}
