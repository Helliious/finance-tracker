package financeTracker.controllers;

import financeTracker.models.dto.category_dto.ResponseCategoryDTO;
import financeTracker.models.pojo.Category;
import financeTracker.services.CategoryService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class CategoryController extends AbstractController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SessionManager sessionManager;

    @GetMapping("/category/{category_id}")
    public ResponseCategoryDTO getById(@PathVariable(name = "category_id") int categoryId,
                                       HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return categoryService.getById(categoryId, userId);
    }

    @GetMapping("/category")
    public List<ResponseCategoryDTO> getAll(HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return categoryService.getAll(userId);
    }

    @PutMapping("/category")
    public ResponseCategoryDTO add(@RequestBody Category category,
                        HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return categoryService.add(category, userId);
    }

    @DeleteMapping("/category/{category_id}/delete")
    public ResponseCategoryDTO delete(@PathVariable(name = "category_id") int categoryId,
                           HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return categoryService.delete(categoryId, userId);
    }
}
