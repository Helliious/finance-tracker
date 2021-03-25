package financeTracker.models.dao;

import financeTracker.models.pojo.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TransactionDAO {
    public Transaction getById(long id){
        //TODO get from database
        return null;
    }
    public ArrayList<Transaction> getAllByUser(long ownerId){
        //TODO get from database
        return new ArrayList<>();
    }

    public ArrayList<Transaction> getAllByAccount(long accountId) {
        //TODO get from database
        return new ArrayList<>();
    }
    public void deleteById(long id){
        //TODO delete from database
    }
}
