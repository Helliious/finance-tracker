package financeTracker.services;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.transaction_dto.ResponseTransactionDTO;
import financeTracker.models.pojo.Transaction;
import financeTracker.models.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public ResponseTransactionDTO getById(int id){
        Optional<Transaction> transaction=transactionRepository.findById(id);
        if (transaction.isPresent()){
            return new ResponseTransactionDTO(transaction.get());
        }
        else{
            throw new NotFoundException("Transaction not found");
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
        if (fakeTransaction.isPresent()){
            throw new  NotFoundException("Transaction doesn't exist");
        }
        ResponseTransactionDTO responseTransaction=new ResponseTransactionDTO(fakeTransaction.get());
        transactionRepository.deleteById(id);
        return responseTransaction;
    }
}
