package financeTracker.controllers;

import financeTracker.models.dto.transaction_dto.*;
import financeTracker.models.pojo.Transaction;
import financeTracker.services.TransactionService;
import financeTracker.utils.SessionManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class TransactionController extends AbstractController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private ModelMapper modelMapper;

    @PutMapping("/transactions/accounts/{account_id}")
    public TransactionWithoutOwnerAndAccountDTO addTransaction(@RequestBody AddTransactionRequestDTO dto,
                                                               @PathVariable(name = "account_id") int accountId,
                                                               HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Transaction transaction = transactionService.addTransactionToAccount(accountId, dto, userId);
        return convertToTransactionWithoutOwnerAndAccountDTO(transaction);
    }

    @GetMapping("/transactions/{transaction_id}")
    public TransactionWithoutOwnerAndAccountDTO getById(@PathVariable(name = "transaction_id") int transactionId,
                                                        HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        Transaction transaction = transactionService.getById(userId, transactionId);
        return convertToTransactionWithoutOwnerAndAccountDTO(transaction);
    }

    @GetMapping("/transactions")
    public List<TransactionWithoutOwnerAndAccountDTO> getAllByUser(HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        List<Transaction> transactions = transactionService.getByOwnerId(userId);
        return transactions.stream()
                .map(this::convertToTransactionWithoutOwnerAndAccountDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/transactions/accounts/{account_id}")
    public List<TransactionWithoutOwnerAndAccountDTO> getAllByAccount(@PathVariable(name = "account_id") int accountId,
                                                                      HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        List<Transaction> transactions = transactionService.getByAccountId(userId, accountId);
        return transactions.stream()
                .map(this::convertToTransactionWithoutOwnerAndAccountDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/transactions/{transaction_id}")
    public TransactionWithoutOwnerAndAccountDTO delete(@PathVariable(name = "transaction_id") int transactionId,
                                                       HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        return transactionService.delete(transactionId, userId);
    }

    @PostMapping("/transactions/{transaction_id}")
    public TransactionWithoutOwnerAndAccountDTO edit(@PathVariable(name = "transaction_id") int transactionId,
                                                     @RequestBody EditTransactionRequestDTO dto,
                                                     HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Transaction transaction = transactionService.editTransaction(transactionId, dto, userId);
        return convertToTransactionWithoutOwnerAndAccountDTO(transaction);
    }

    @PostMapping("/transactions/filter")
    public List<TransactionWithoutOwnerAndAccountDTO> filter(@RequestBody FilterTransactionRequestDTO dto,
                                                             HttpSession session){
        int userId=sessionManager.getLoggedId(session);
        List<Transaction> transactions = transactionService.filter(userId, dto);
        return transactions.stream()
                .map(this::convertToTransactionWithoutOwnerAndAccountDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/transactions/{transaction_id}/accounts/{account_id}/budgets/{budget_id}")
    public TransactionWithoutOwnerAndAccountDTO addTransactionToBudget(@PathVariable(name="account_id") int accountId,
                                                                       @PathVariable(name="budget_id") int budgetId,
                                                                       @PathVariable(name = "transaction_id") int transactionId,
                                                                       HttpSession session){
        int userId=sessionManager.getLoggedId(session);
        Transaction transaction = transactionService.addTransactionToBudget(userId, accountId, budgetId, transactionId);
        return convertToTransactionWithoutOwnerAndAccountDTO(transaction);
    }

    private TransactionWithoutOwnerAndAccountDTO convertToTransactionWithoutOwnerAndAccountDTO(Transaction transaction) {
        return modelMapper.map(transaction, TransactionWithoutOwnerAndAccountDTO.class);
    }
}
