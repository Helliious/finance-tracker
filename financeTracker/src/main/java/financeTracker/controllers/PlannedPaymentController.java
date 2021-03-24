package financeTracker.controllers;

import financeTracker.models.dao.PlannedPaymentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlannedPaymentController extends AbstractController{
    @Autowired
    private PlannedPaymentDao plannedPaymentDao;

    //TODO methods of controller
}
