package financeTracker.controllers;

import financeTracker.models.dto.transaction_dto.*;
import financeTracker.services.TransactionService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@RestController
public class TransactionController extends AbstractController {
    @Autowired
    private TransactionService transactionService;
    @Autowired private SessionManager sessionManager;

    @PutMapping("/transactions/accounts/{account_id}")
    public TransactionWithoutOwnerAndAccountDTO addTransaction(@RequestBody AddTransactionRequestDTO dto,
                                                               @PathVariable("account_id") int accountId,
                                                               HttpSession session){
        dto.setCreateTime(new Timestamp(System.currentTimeMillis()));
        int userId=sessionManager.getLoggedId(session);
        return transactionService.addTransactionToAcc(accountId,dto,userId);
    }

    @GetMapping("/transactions/{transaction_id}")
    public TransactionWithoutOwnerAndAccountDTO getById(@PathVariable(name="transaction_id") int transactionId,
                                                        HttpSession session){
       int userId=sessionManager.getLoggedId(session);
        return transactionService.getById(userId,transactionId);
    }

    @GetMapping("/transactions/users")
    public ArrayList<TransactionWithoutOwnerAndAccountDTO> getAllByUser(HttpSession session){
        int userId=sessionManager.getLoggedId(session);
        return transactionService.getByOwnerId(userId);
    }

    @GetMapping("/transactions/accounts/{account_id}")
    public ArrayList<TransactionWithoutOwnerAndAccountDTO> getAllByAccount(@PathVariable("account_id") int accountId,
                                                                           HttpSession session){
        int userId=sessionManager.getLoggedId(session);
        return transactionService.getByAccountId(userId,accountId);
    }

    @DeleteMapping("/transactions/{transaction_id}")
    public TransactionWithoutOwnerAndAccountDTO delete(@PathVariable(name="transaction_id") int transactionId,
                                                       HttpSession session){
        int userId=sessionManager.getLoggedId(session);
        return transactionService.delete(transactionId,userId);
    }

    @PostMapping("/transactions")
    public TransactionWithoutOwnerAndAccountDTO edit(@PathVariable(name="transaction_id") int transactionId,
                                                     @RequestBody EditTransactionRequestDTO dto,
                                                     HttpSession session) {
        int userId=sessionManager.getLoggedId(session);
        return transactionService.editTransaction(transactionId,dto,userId);
    }

    @PostMapping("/transactions/{transaction_id}}")
    public List<TransactionWithoutOwnerAndAccountDTO> filter(@RequestBody FilterTransactionRequestDTO dto,
                                                             HttpSession session){
        int userId=sessionManager.getLoggedId(session);
        return transactionService.filter(userId,dto);
    }
    @PostMapping("transactions/{transaction_id}/budgets/{budget_id}")
    public TransactionWithoutOwnerAndAccountDTO addTransactionToBudget(@PathVariable(name="transaction_id") int transactionId,
                                                                       @PathVariable(name="budget_id") int budgetId,
                                                                       HttpSession session){
        int userId=sessionManager.getLoggedId(session);
        return transactionService.addTransactionToBudget(userId,budgetId,transactionId);
    }

}
