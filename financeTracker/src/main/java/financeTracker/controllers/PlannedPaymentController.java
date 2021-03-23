package financeTracker.controllers;

import financeTracker.models.plannedPayments.PlannedPaymentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlannedPaymentController {
    @Autowired
    private PlannedPaymentDao plannedPaymentDao;

    //TODO methods of controller
}
