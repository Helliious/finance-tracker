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

    @PutMapping("/users/{user_id}/accounts/{account_id}/planned_payments")
    public UserWithoutPassDTO add(@PathVariable(name = "user_id") int userId,
                                  @PathVariable(name = "account_id") int accountId,
                                  @RequestBody PlannedPayment plannedPayment,
                                  HttpSession session) {
        String message = "Cannot add planned payments to other users!";
        SessionManager.validateSession(session, message, userId);
        plannedPayment.setDueTime(new Timestamp(System.currentTimeMillis()));
        return plannedPaymentsService.add(plannedPayment, userId, accountId);
    }

    @GetMapping("/users/{user_id}/accounts/{account_id}/planned_payments/{planned_payment_id}")
    public ResponsePlannedPaymentDTO getById(@PathVariable(name = "user_id") int userId,
                                             @PathVariable(name = "account_id") int accountId,
                                             @PathVariable(name = "planned_payment_id") int plannedPaymentId,
                                             HttpSession session) {
        String message = "Cannot see planned payments of other users!";
        SessionManager.validateSession(session, message, userId);
        return plannedPaymentsService.getById(accountId, plannedPaymentId);
    }

    @GetMapping("/users/{user_id}/accounts/{account_id}/planned_payments")
    public List<ResponsePlannedPaymentDTO> getAll(@PathVariable(name = "user_id") int userId,
                                                  @PathVariable(name = "account_id") int accountId,
                                                  HttpSession session) {
        String message = "Cannot see planned payments of other users!";
        SessionManager.validateSession(session, message, userId);
        return plannedPaymentsService.getAll(accountId);
    }

    @DeleteMapping("/users/{user_id}/accounts/{account_id}/planned_payments/{planned_payment_id}/delete")
    public ResponsePlannedPaymentDTO delete(@PathVariable(name = "user_id") int userId,
                                            @PathVariable(name = "account_id") int accountId,
                                            @PathVariable(name = "planned_payment_id") int plannedPaymentId,
                                            HttpSession session) {
        String message = "Cannot delete planned payments of other users!";
        SessionManager.validateSession(session, message, userId);
        return plannedPaymentsService.delete(accountId, plannedPaymentId);
    }

    @PutMapping("/users/{user_id}/accounts/{account_id}/planned_payments/{planned_payment_id}/edit")
    public UserWithoutPassDTO edit(@PathVariable(name = "user_id") int userId,
                                   @PathVariable(name = "account_id") int accountId,
                                   @PathVariable(name = "planned_payment_id") int plannedPaymentId,
                                   @RequestBody ResponsePlannedPaymentDTO responsePlannedPaymentDTO,
                                   HttpSession session) {
        String message = "Cannot edit planned payments of other users!";
        SessionManager.validateSession(session, message, userId);
        return plannedPaymentsService.edit(responsePlannedPaymentDTO, accountId, plannedPaymentId);
    }
}
