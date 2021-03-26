package financeTracker.controllers;

import financeTracker.models.pojo.Category;
import financeTracker.services.CategoryService;
import financeTracker.utils.SessionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class CategoryController extends AbstractController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/users/{id}/category")
    public List<Category> getAll(@PathVariable int id, HttpSession session) {
        String message = "Cannot show categories of other users!";
        SessionValidator.validateSession(session, message, id);
        return categoryService.getAll();
    }

    @PutMapping("/users/{id}/category/add_category")
    public Category add(@PathVariable int id,
                        @RequestBody Category category,
                        HttpSession session) {
        String message = "Cannot add categories for other users!";
        SessionValidator.validateSession(session, message, id);
        return categoryService.add(category);
    }

    @DeleteMapping("/users/{user_id}/category/{category_id}/delete_category")
    public Category delete(@PathVariable(name = "user_id") int userId,
                           @PathVariable(name = "category_id") int categoryId,
                           HttpSession session) {
        String message = "Cannot delete categories for other users!";
        SessionValidator.validateSession(session, message, userId);
        return categoryService.delete(categoryId);
    }
}
