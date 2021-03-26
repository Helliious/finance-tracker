package financeTracker.controllers;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.transaction_dto.AddTransactionRequestDTO;
import financeTracker.models.dto.transaction_dto.ResponseTransactionDTO;
import financeTracker.models.dao.TransactionDAO;
import financeTracker.models.pojo.Account;
import financeTracker.models.repository.AccountRepository;
import financeTracker.services.TransactionService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Optional;

@RestController
public class TransactionController extends AbstractController {
    @Autowired
    private  TransactionDAO transactionDao;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/accounts/{account_id}/transactions")
    public ResponseTransactionDTO addTransaction(@RequestBody AddTransactionRequestDTO dto
            ,@PathVariable("account_id") int accountId
            ,HttpSession session){
        SessionManager.validateSession(session,"Cannot add to other user transaction!!",dto.getUserId());
        return transactionService.addTransactionToAcc(accountId,dto);
    }
    @GetMapping("/transactions/{id}")
    public ResponseTransactionDTO getById(@PathVariable int id,HttpSession session){
        ResponseTransactionDTO transaction=transactionService.getById(id);
        int ownerId=transaction.getAccount().getOwner().getId();
        SessionManager.validateSession(session,"Cannot get other user transaction!!",ownerId);
        return transactionService.getById(id);
    }
    @GetMapping("/users/{owner_id}/transactions")
    public ArrayList<ResponseTransactionDTO> getAllByUser(@PathVariable("owner_id") int ownerId
            ,HttpSession session){
        SessionManager.validateSession(session,"Cannot see other users transactions!!",ownerId);
        return transactionService.getByOwnerId(ownerId);
    }
    @GetMapping("/accounts/{account_id}/transactions")
    public ArrayList<ResponseTransactionDTO> getAllByAccount(@PathVariable("account_id") int accountId
            ,HttpSession session){
        Optional<Account> optionalAccount=accountRepository.findById(accountId);
        if (optionalAccount.isEmpty()){
            throw new NotFoundException("Account does not exist..");
        }
        Account account=optionalAccount.get();
        int ownerId=account.getOwner().getId();
        SessionManager.validateSession(session,"Cannot see other users account transactions!!",ownerId);
        return transactionService.getByAccountId(accountId);
    }
    @DeleteMapping("/transactions/delete/{id}")
    public void delete(@PathVariable int id,HttpSession session){
        ResponseTransactionDTO transaction=transactionService.getById(id);
        int ownerId=transaction.getAccount().getOwner().getId();
        SessionManager.validateSession(session,"Cannot delete other users transaction!!",ownerId);
        transactionDao.deleteById(id);
    }
}
