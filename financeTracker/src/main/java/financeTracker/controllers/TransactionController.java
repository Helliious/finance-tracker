package financeTracker.controllers;

import financeTracker.models.dto.transaction_dto.*;
import financeTracker.models.pojo.Transaction;
import financeTracker.services.TransactionService;
import financeTracker.utils.SessionManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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

    @PutMapping("/accounts/{account_id}/transactions")
    public RasponseTransactionDTO addTransaction(@Valid @RequestBody AddTransactionRequestDTO dto,
                                                 @PathVariable(name = "account_id") int accountId,
                                                 HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Transaction transaction = transactionService.addTransactionToAccount(accountId, dto, userId);
        return convertToTransactionWithoutOwnerAndAccountDTO(transaction);
    }

    @GetMapping("/accounts/{account_id}/transactions/{transaction_id}")
    public RasponseTransactionDTO getById(@PathVariable(name = "account_id") int accountId,
                                          @PathVariable(name = "transaction_id") int transactionId,
                                          HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        Transaction transaction = transactionService.getById(userId, transactionId, accountId);
        return convertToTransactionWithoutOwnerAndAccountDTO(transaction);
    }

    @GetMapping("/transactions")
    public List<RasponseTransactionDTO> getAllByUser(HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        List<Transaction> transactions = transactionService.getByOwnerId(userId);
        return transactions.stream()
                .map(this::convertToTransactionWithoutOwnerAndAccountDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/accounts/{account_id}/transactions")
    public List<RasponseTransactionDTO> getAllByAccount(@PathVariable(name = "account_id") int accountId,
                                                        HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        List<Transaction> transactions = transactionService.getByAccountId(userId, accountId);
        return transactions.stream()
                .map(this::convertToTransactionWithoutOwnerAndAccountDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/accounts/{account_id}/transactions/{transaction_id}")
    public RasponseTransactionDTO delete(@PathVariable(name = "account_id") int accountId,
                                         @PathVariable(name = "transaction_id") int transactionId,
                                         HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        return transactionService.delete(transactionId, userId, accountId);
    }

    @PostMapping("/accounts/{account_id}/transactions/{transaction_id}")
    public RasponseTransactionDTO edit(@PathVariable(name = "account_id") int accountId,
                                       @PathVariable(name = "transaction_id") int transactionId,
                                       @Valid @RequestBody EditTransactionRequestDTO dto,
                                       HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Transaction transaction = transactionService.editTransaction(transactionId, dto, userId, accountId);
        return convertToTransactionWithoutOwnerAndAccountDTO(transaction);
    }

    @PostMapping("/transactions")
    public List<RasponseTransactionDTO> filter(@Valid @RequestBody FilterTransactionRequestDTO dto,
                                               HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        List<Transaction> transactions = transactionService.filter(userId, dto);
        return transactions.stream()
                .map(this::convertToTransactionWithoutOwnerAndAccountDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/accounts/{account_id}/budgets/{budget_id}/transactions/{transaction_id}")
    public RasponseTransactionDTO addTransactionToBudget(@PathVariable(name="account_id") int accountId,
                                                         @PathVariable(name="budget_id") int budgetId,
                                                         @PathVariable(name = "transaction_id") int transactionId,
                                                         HttpSession session){
        int userId = sessionManager.getLoggedId(session);
        Transaction transaction = transactionService.addTransactionToBudget(userId, accountId, budgetId, transactionId);
        return convertToTransactionWithoutOwnerAndAccountDTO(transaction);
    }

    private RasponseTransactionDTO convertToTransactionWithoutOwnerAndAccountDTO(Transaction transaction) {
        return modelMapper.map(transaction, RasponseTransactionDTO.class);
    }
}
