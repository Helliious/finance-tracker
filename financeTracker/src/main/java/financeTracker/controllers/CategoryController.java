package financeTracker.controllers;

import financeTracker.models.categories.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {
    @Autowired
    private CategoryDao categoryDao;
    //TODO methods for category controller
}
