package financeTracker.controllers;

import financeTracker.models.dao.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController extends AbstractController {
    @Autowired
    private CategoryDao categoryDao;
    //TODO methods for category controller
}
