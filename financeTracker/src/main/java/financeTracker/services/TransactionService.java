package financeTracker.services;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.transactionsDTO.AddTransactionRequestDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.dto.transaction_dto.ResponseTransactionDTO;
import financeTracker.models.pojo.Transaction;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.AccountRepository;
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

    public ResponseTransactionDTO getById(int id){
        Optional<Transaction> optionalTransaction=transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()){
            throw new NotFoundException("Transaction not found");
        }
        else{
            return new ResponseTransactionDTO(optionalTransaction.get());
        }
    }
    public ArrayList<ResponseTransactionDTO> getByOwnerId(int id){
         ArrayList<Transaction> transactions=transactionRepository.findTransactionsByOwner_Id(id);
         if (transactions.isEmpty()){
             throw new NotFoundException("User don't have transactions");
         }
        ArrayList<ResponseTransactionDTO> responseTransactions =new ArrayList<>();
        for (Transaction transaction:transactions){
            ResponseTransactionDTO responseTransaction=new ResponseTransactionDTO(transaction);
            responseTransactions.add(responseTransaction);
        }
        return responseTransactions;
    }
    public ArrayList<ResponseTransactionDTO> getByAccountId(int id){
        ArrayList<Transaction> transactions=transactionRepository.findTransactionsByAccount_Id(id);
        if (transactions.isEmpty()){
            throw new NotFoundException("This account dont'have transactions");
        }
        ArrayList<ResponseTransactionDTO> responseTransactions=new ArrayList<>();
        for (Transaction transaction:transactions){
            ResponseTransactionDTO responseTransaction=new ResponseTransactionDTO(transaction);
            responseTransactions.add(responseTransaction);
        }
        return responseTransactions;
    }
    public ResponseTransactionDTO delete(int id){
        Optional<Transaction> fakeTransaction=transactionRepository.findById(id);
        if (fakeTransaction.isEmpty()){
            throw new  NotFoundException("Transaction doesn't exist");
        }
        ResponseTransactionDTO responseTransaction=new ResponseTransactionDTO(fakeTransaction.get());
        transactionRepository.deleteById(id);
        return responseTransaction;
    }
    public ResponseTransactionDTO addTransactionToAcc(int accountId, AddTransactionRequestDTO dto){
        Optional<Account> optionalAccount= accountRepository.findById(accountId);
        Optional<User> optionalUser=userRepository.findById(dto.getUserId());
        if (optionalAccount.isEmpty()){
            throw new  NotFoundException("Account doesn't exist");
        }
        if (optionalUser.isEmpty()){
            throw new NotFoundException("User doesn't exist");
        }
        Transaction transaction=new Transaction(dto);
        Account account=optionalAccount.get();
        User owner=optionalUser.get();
        transaction.setOwner(owner);
        transaction.setAccount(account);
        transactionRepository.save(transaction);
        return new ResponseTransactionDTO(transaction);
    }
}
