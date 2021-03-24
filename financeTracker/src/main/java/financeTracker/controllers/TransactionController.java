package financeTracker.controllers;

import financeTracker.models.pojo.Transaction;
import financeTracker.models.dao.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class TransactionController extends AbstractController {
    @Autowired
    private TransactionDao transactionDao;

    @GetMapping("/transactions/{id}")
    public Transaction getById(@PathVariable long id){
        Transaction transaction=transactionDao.getById(id);
        return transaction;
    }
    @GetMapping("/transactions/owner/{ownerId}")
    public ArrayList<Transaction> getByUser(@PathVariable long ownerId){
        ArrayList<Transaction> userTransactions=transactionDao.getAllByUser(ownerId);
        return userTransactions;
    }
    @GetMapping("/transactions/account/{accountId}")
    public ArrayList<Transaction> getByAccount(long accountId){
        ArrayList<Transaction> accountTransactions=transactionDao.getAllByAccount(accountId);
        return accountTransactions;
    }
    @DeleteMapping("/transactions/delete/{id}")
    public void delete(@PathVariable long id){
        transactionDao.deleteById(id);
    }
}
