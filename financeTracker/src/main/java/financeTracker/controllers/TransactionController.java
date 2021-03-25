package financeTracker.controllers;

import financeTracker.models.dto.transactionsDTO.ResponseTransactionDTO;
import financeTracker.models.dao.TransactionDAO;
import financeTracker.models.pojo.Transaction;
import financeTracker.models.repository.UserRepository;
import financeTracker.services.TransactionService;
import financeTracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class TransactionController extends AbstractController {
    @Autowired
    private  TransactionDAO transactionDao;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;

    @PostMapping("/accounts/{account_id}/transactions")
    public ResponseTransactionDTO addTransaction(@RequestBody Transaction transaction,
                                                 @PathVariable("account_id") int accountId){
        return  new ResponseTransactionDTO(transaction);
    }
    @GetMapping("/transactions/{id}")
    public ResponseTransactionDTO getById(@PathVariable int id){
     return transactionService.getById(id);
    }
    @GetMapping("/users/{owner_id}/transactions")
    public ArrayList<ResponseTransactionDTO> getByUser(@PathVariable("owner_id") int ownerId){
        return transactionService.getByOwnerId(ownerId);
    }
    @GetMapping("/accounts/{account_id}/transactions")
    public ArrayList<ResponseTransactionDTO> getByAccount(@PathVariable("account_id") int accountId){
        return transactionService.getByAccountId(accountId);
    }
    @DeleteMapping("/transactions/delete/{id}")
    public void delete(@PathVariable long id){
        transactionDao.deleteById(id);
    }

}
