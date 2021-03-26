package financeTracker.controllers;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.models.pojo.Category;
import financeTracker.services.CategoryService;
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
        if (session.getAttribute("LoggedUser") == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            int loggedId = (int) session.getAttribute("LoggedUser");
            if (loggedId != id) {
                throw new BadRequestException("Cannot show categories of other users!");
            }
        }
        return categoryService.getAll();
    }

    @PutMapping("/users/{id}/category/add_category")
    public Category add(@PathVariable int id,
                        @RequestBody Category category,
                        HttpSession session) {
        if (session.getAttribute("LoggedUser") == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            int loggedId = (int) session.getAttribute("LoggedUser");
            if (loggedId != id) {
                throw new BadRequestException("Cannot add categories for other users!");
            }
        }
        return categoryService.add(category);
    }

    @DeleteMapping("/users/{user_id}/category/{category_id}/delete_category")
    public Category delete(@PathVariable(name = "user_id") int userId,
                           @PathVariable(name = "category_id") int categoryId,
                           HttpSession session) {
        if (session.getAttribute("LoggedUser") == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            int loggedId = (int) session.getAttribute("LoggedUser");
            if (loggedId != userId) {
                throw new BadRequestException("Cannot delete categories for other users!");
            }
        }
        return categoryService.delete(categoryId);
    }
}
