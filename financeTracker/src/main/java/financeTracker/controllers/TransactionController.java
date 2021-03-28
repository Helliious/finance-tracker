package financeTracker.controllers;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.transaction_dto.*;
import financeTracker.models.dao.TransactionDAO;
import financeTracker.models.pojo.Account;
import financeTracker.models.repository.AccountRepository;
import financeTracker.services.TransactionService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
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

    @PostMapping("/accounts/{account_id}/transaction/add")
    public TransactionWithoutOwnerDTO addTransaction(@RequestBody AddTransactionRequestDTO dto
            , @PathVariable("account_id") int accountId
            , HttpSession session){
        dto.setCreateTime(new Timestamp(System.currentTimeMillis()));
        SessionManager.validateSession(session,"Cannot add to other user account!!",dto.getUserId());
        return transactionService.addTransactionToAcc(accountId,dto);
    }
    @GetMapping("/users/{owner_id}/transactions/{transaction_id}")
    public TransactionWithoutOwnerDTO getById(@PathVariable(name="owner_id") int ownerId
            ,@PathVariable(name="transaction_id") int transactionId
            ,HttpSession session){
        SessionManager.validateSession(session,"Cannot get other user transaction!!",ownerId);
        return transactionService.getById(transactionId);
    }
    @GetMapping("/users/{owner_id}/transactions")
    public ArrayList<TransactionWithoutOwnerDTO> getAllByUser(@PathVariable("owner_id") int ownerId
            ,HttpSession session){
        SessionManager.validateSession(session,"Cannot see other users transactions!!",ownerId);
        return transactionService.getByOwnerId(ownerId);
    }
    @GetMapping("/accounts/{account_id}/transactions")
    public ArrayList<TransactionWithoutOwnerDTO> getAllByAccount(@PathVariable("account_id") int accountId
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
    @DeleteMapping("/users/{user_id}/transactions/delete/{transaction_id}")
    public TransactionWithoutOwnerDTO delete(@PathVariable(name="user_id") int userId
            ,@PathVariable(name="transaction_id") int transactionId
            ,HttpSession session){
        SessionManager.validateSession(session,"Cannot delete other users transaction!!",userId);
       return transactionService.delete(transactionId,userId);
    }
    @PutMapping("/users/{user_id}/transactions/{transaction_id}/edit")
    public TransactionWithoutOwnerDTO edit(@PathVariable(name = "user_id") int userId
            ,@PathVariable(name="transaction_id") int transactionId,
                                   @RequestBody EditTransactionRequestDTO dto,
                                   HttpSession session) {
        SessionManager.validateSession(session,"You can't modify other users transactions",userId);
       return transactionService.editTransaction(transactionId,dto,userId);
    }
    @PostMapping("users/{user_id}/transactions/filter")
    public ArrayList<TransactionWithoutOwnerDTO> filter(@PathVariable(name="user_id") int userId
            , @RequestBody FilterTransactionRequestDTO dto
            ,HttpSession session){
        SessionManager.validateSession(session,"You can't filter other users transactions",userId);
        return transactionService.filter(userId,dto);
    }
}
