package financeTracker.models.transactions;

import com.sun.xml.bind.v2.TODO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TransactionDao {
    public Transaction getById(long id){
        //TODO get from database
        return new Transaction();
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
