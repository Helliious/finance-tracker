package financeTracker.controllers;

import financeTracker.models.dto.planned_payment_dto.FilterPlannedPaymentRequestDTO;
import financeTracker.models.dto.planned_payment_dto.ResponsePlannedPaymentDTO;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.PlannedPayment;
import financeTracker.models.pojo.User;
import financeTracker.services.PlannedPaymentsService;
import financeTracker.utils.SessionManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PlannedPaymentController extends AbstractController{
    @Autowired
    private PlannedPaymentsService plannedPaymentsService;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private ModelMapper modelMapper;

    @PutMapping("/accounts/{account_id}/planned_payments")
    public UserWithoutPassDTO add(@PathVariable(name = "account_id") int accountId,
                                  @RequestBody PlannedPayment plannedPayment,
                                  HttpSession session) {
        int userId = sessionManager.validateSession(session);
        User user = plannedPaymentsService.add(plannedPayment, userId, accountId);
        return convertToUserWithoutPassDTO(user);
    }

    @GetMapping("/accounts/{account_id}/planned_payments/{planned_payment_id}")
    public ResponsePlannedPaymentDTO getById(@PathVariable(name = "account_id") int accountId,
                                             @PathVariable(name = "planned_payment_id") int plannedPaymentId,
                                             HttpSession session) {
        int userId = sessionManager.validateSession(session);
        PlannedPayment plannedPayment = plannedPaymentsService.getById(accountId, userId, plannedPaymentId);
        return convertToResponsePlannedPaymentDTO(plannedPayment);
    }

    @GetMapping("/accounts/{account_id}/planned_payments")
    public List<ResponsePlannedPaymentDTO> getAll(@PathVariable(name = "account_id") int accountId,
                                                  HttpSession session) {
        int userId = sessionManager.validateSession(session);
        List<PlannedPayment> plannedPayments = plannedPaymentsService.getAll(accountId, userId);
        return plannedPayments.stream()
                .map(this::convertToResponsePlannedPaymentDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/accounts/{account_id}/planned_payments/{planned_payment_id}")
    public ResponsePlannedPaymentDTO delete(@PathVariable(name = "account_id") int accountId,
                                            @PathVariable(name = "planned_payment_id") int plannedPaymentId,
                                            HttpSession session) {
        int userId = sessionManager.validateSession(session);
        PlannedPayment plannedPayment = plannedPaymentsService.delete(accountId, userId, plannedPaymentId);
        return convertToResponsePlannedPaymentDTO(plannedPayment);
    }

    @PostMapping("/accounts/{account_id}/planned_payments/{planned_payment_id}")
    public ResponsePlannedPaymentDTO edit(@PathVariable(name = "account_id") int accountId,
                                   @PathVariable(name = "planned_payment_id") int plannedPaymentId,
                                   @RequestBody ResponsePlannedPaymentDTO responsePlannedPaymentDTO,
                                   HttpSession session) {
        int userId = sessionManager.validateSession(session);
        PlannedPayment plannedPayment = plannedPaymentsService.edit(responsePlannedPaymentDTO, accountId, userId, plannedPaymentId);
        return convertToResponsePlannedPaymentDTO(plannedPayment);
    }

    @PostMapping("/planned_payments/filter")
    public List<ResponsePlannedPaymentDTO> filter(@RequestBody FilterPlannedPaymentRequestDTO plannedPaymentRequestDTO,
                                               HttpSession session){
        int userId = sessionManager.validateSession(session);
        List<PlannedPayment> plannedPayments = plannedPaymentsService.filter(userId, plannedPaymentRequestDTO);
        return plannedPayments.stream()
                .map(this::convertToResponsePlannedPaymentDTO)
                .collect(Collectors.toList());
    }

    private ResponsePlannedPaymentDTO convertToResponsePlannedPaymentDTO(PlannedPayment plannedPayment) {
        return modelMapper.map(plannedPayment, ResponsePlannedPaymentDTO.class);
    }

    private UserWithoutPassDTO convertToUserWithoutPassDTO(User user) {
        return modelMapper.map(user, UserWithoutPassDTO.class);
    }
}
