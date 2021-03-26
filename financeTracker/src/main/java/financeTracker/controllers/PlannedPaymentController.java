package financeTracker.controllers;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.models.dto.planned_payment_dto.ResponsePlannedPaymentDTO;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.PlannedPayment;
import financeTracker.services.PlannedPaymentsService;
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
        if (session.getAttribute("LoggedUser") == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            int loggedId = (int) session.getAttribute("LoggedUser");
            if (loggedId != userId) {
                throw new BadRequestException("Cannot add planned payments to other users!");
            }
        }
        plannedPayment.setDueTime(new Timestamp(System.currentTimeMillis()));
        return plannedPaymentsService.add(plannedPayment, userId, accountId);
    }

    @GetMapping("/users/{user_id}/accounts/{account_id}/planned_payments/{planned_payment_id}")
    public ResponsePlannedPaymentDTO getById(@PathVariable(name = "user_id") int userId,
                                             @PathVariable(name = "account_id") int accountId,
                                             @PathVariable(name = "planned_payment_id") int plannedPaymentId,
                                             HttpSession session) {
        if (session.getAttribute("LoggedUser") == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            int loggedId = (int) session.getAttribute("LoggedUser");
            if (loggedId != userId) {
                throw new BadRequestException("Cannot see planned payments of other users!");
            }
        }
        return plannedPaymentsService.getById(accountId, plannedPaymentId);
    }

    @GetMapping("/users/{user_id}/accounts/{account_id}/planned_payments")
    public List<ResponsePlannedPaymentDTO> getAll(@PathVariable(name = "user_id") int userId,
                                                  @PathVariable(name = "account_id") int accountId,
                                                  HttpSession session) {
        if (session.getAttribute("LoggedUser") == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            int loggedId = (int) session.getAttribute("LoggedUser");
            if (loggedId != userId) {
                throw new BadRequestException("Cannot see planned payments of other users!");
            }
        }
        return plannedPaymentsService.getAll(accountId);
    }
}
