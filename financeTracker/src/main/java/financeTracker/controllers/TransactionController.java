package financeTracker.controllers;

import financeTracker.models.pojo.Transaction;
import financeTracker.models.dao.TransactionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class TransactionController extends AbstractController {
    @Autowired
    private TransactionDAO transactionDao;

    @GetMapping("/transactions/{id}")
    public Transaction getById(@PathVariable long id){
        Transaction transaction=transactionDao.getById(id);
        return transaction;
    }
    @GetMapping("/users/{owner_id}/{transactions}")
    public ArrayList<Transaction> getByUser(@PathVariable("owner_id") long ownerId){
        ArrayList<Transaction> userTransactions=transactionDao.getAllByUser(ownerId);
        return userTransactions;
    }
    @GetMapping("/accounts/{account_id}/transactions")
    public ArrayList<Transaction> getByAccount(@PathVariable("account_id") long accountId){
        ArrayList<Transaction> accountTransactions=transactionDao.getAllByAccount(accountId);
        return accountTransactions;
    }
    @DeleteMapping("/transactions/delete/{id}")
    public void delete(@PathVariable long id){
        transactionDao.deleteById(id);
    }
}
