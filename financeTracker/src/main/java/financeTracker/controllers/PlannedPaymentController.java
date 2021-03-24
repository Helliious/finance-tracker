package financeTracker.controllers;

import financeTracker.models.dao.PlannedPaymentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlannedPaymentController extends AbstractController{
    @Autowired
    private PlannedPaymentDAO plannedPaymentDao;

    //TODO methods of controller
}
