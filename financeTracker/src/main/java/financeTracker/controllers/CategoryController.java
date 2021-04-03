package financeTracker.controllers;

import financeTracker.models.dto.category_dto.CategoryExpensesDTO;
import financeTracker.models.dto.category_dto.ResponseCategoryDTO;
import financeTracker.models.pojo.Category;
import financeTracker.services.CategoryService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController extends AbstractController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SessionManager sessionManager;

    @GetMapping("/categories/{category_id}")
    public ResponseCategoryDTO getById(@PathVariable(name = "category_id") int categoryId,
                                       HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Category category = categoryService.getById(categoryId, userId);
        return new ResponseCategoryDTO(category);
    }

    @GetMapping("/categories")
    public List<ResponseCategoryDTO> getAll(HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        List<Category> categories = categoryService.getAll(userId);
        List<ResponseCategoryDTO> resultCategories = new ArrayList<>();
        for (Category c : categories) {
            resultCategories.add(new ResponseCategoryDTO(c));
        }
        return resultCategories;
    }

    @PutMapping("/categories")
    public ResponseCategoryDTO add(@RequestBody Category category,
                        HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Category resultCategory = categoryService.add(category, userId);
        return new ResponseCategoryDTO(resultCategory);
    }

    @DeleteMapping("/categories/{category_id}")
    public ResponseCategoryDTO delete(@PathVariable(name = "category_id") int categoryId,
                           HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        Category category = categoryService.delete(categoryId, userId);
        return new ResponseCategoryDTO(category);
    }
//
//    @GetMapping("categories/references")
//    public List<CategoryExpensesDTO> referenceExpenses(HttpSession session) {
//        sessionManager.getLoggedId(session);
//        return categoryService.referenceOverallExpensesByCategory();
//    }
}
