package financeTracker.controllers;

import financeTracker.models.dto.category_dto.ResponseCategoryDTO;
import financeTracker.models.dto.user_dto.UserWithoutPassDTO;
import financeTracker.models.pojo.Category;
import financeTracker.services.CategoryService;
import financeTracker.utils.SessionManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CategoryController extends AbstractController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/categories/{category_id}")
    public ResponseCategoryDTO getById(@PathVariable(name = "category_id") int categoryId,
                                       HttpSession session) {
        int userId = sessionManager.validateSession(session);
        Category category = categoryService.getById(categoryId, userId);
        return convertToResponseCategoryDTO(category);
    }

    @GetMapping("/categories")
    public List<ResponseCategoryDTO> getAll(HttpSession session) {
        int userId = sessionManager.validateSession(session);
        List<Category> categories = categoryService.getAll(userId);
        return categories.stream()
                .map(this::convertToResponseCategoryDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/categories")
    public ResponseCategoryDTO add(@RequestBody Category category,
                        HttpSession session) {
        int userId = sessionManager.validateSession(session);
        Category resultCategory = categoryService.add(category, userId);
        return convertToResponseCategoryDTO(resultCategory);
    }

    @DeleteMapping("/categories/{category_id}")
    public ResponseCategoryDTO delete(@PathVariable(name = "category_id") int categoryId,
                           HttpSession session) {
        int userId = sessionManager.validateSession(session);
        Category category = categoryService.delete(categoryId, userId);
        return convertToResponseCategoryDTO(category);
    }

    private ResponseCategoryDTO convertToResponseCategoryDTO(Category category) {
        return modelMapper.map(category, ResponseCategoryDTO.class);
    }
}
