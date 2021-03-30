package financeTracker.controllers;

import financeTracker.models.dto.planned_payment_dto.ResponsePlannedPaymentDTO;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.PlannedPayment;
import financeTracker.services.PlannedPaymentsService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

@RestController
public class PlannedPaymentController extends AbstractController{
    @Autowired
    private PlannedPaymentsService plannedPaymentsService;
    @Autowired
    private SessionManager sessionManager;

    @PutMapping("/accounts/{account_id}/planned_payments")
    public UserWithoutPassDTO add(@PathVariable(name = "account_id") int accountId,
                                  @RequestBody PlannedPayment plannedPayment,
                                  HttpSession session) {
        int userId = sessionManager.validateSession(session);
        plannedPayment.setDueTime(new Timestamp(System.currentTimeMillis()));
        return plannedPaymentsService.add(plannedPayment, userId, accountId);
    }

    @GetMapping("/accounts/{account_id}/planned_payments/{planned_payment_id}")
    public ResponsePlannedPaymentDTO getById(@PathVariable(name = "account_id") int accountId,
                                             @PathVariable(name = "planned_payment_id") int plannedPaymentId,
                                             HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return plannedPaymentsService.getById(accountId, userId, plannedPaymentId);
    }

    @GetMapping("/accounts/{account_id}/planned_payments")
    public List<ResponsePlannedPaymentDTO> getAll(@PathVariable(name = "account_id") int accountId,
                                                  HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return plannedPaymentsService.getAll(accountId, userId);
    }

    @DeleteMapping("/accounts/{account_id}/planned_payments/{planned_payment_id}")
    public ResponsePlannedPaymentDTO delete(@PathVariable(name = "account_id") int accountId,
                                            @PathVariable(name = "planned_payment_id") int plannedPaymentId,
                                            HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return plannedPaymentsService.delete(accountId, userId, plannedPaymentId);
    }

    @PostMapping("/accounts/{account_id}/planned_payments/{planned_payment_id}")
    public ResponsePlannedPaymentDTO edit(@PathVariable(name = "account_id") int accountId,
                                   @PathVariable(name = "planned_payment_id") int plannedPaymentId,
                                   @RequestBody ResponsePlannedPaymentDTO responsePlannedPaymentDTO,
                                   HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return plannedPaymentsService.edit(responsePlannedPaymentDTO, accountId, userId, plannedPaymentId);
    }

    //TODO: add filter to planned payments
}
