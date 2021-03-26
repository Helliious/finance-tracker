package financeTracker.controllers;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.PlannedPayment;
import financeTracker.services.PlannedPaymentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

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
}
