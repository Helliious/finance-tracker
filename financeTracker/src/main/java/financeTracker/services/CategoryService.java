package financeTracker.services;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.category_dto.CategoryWithoutPlannedPaymentsDTO;
import financeTracker.models.pojo.Category;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    public Category add(Category category, int userId) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw new AuthenticationException("User not found!");
        }
        category.setOwner(optUser.get());
        return categoryRepository.save(category);
    }

    public Category delete(int categoryId, int userId) {
        Category category = categoryRepository.findByIdAndOwnerId(categoryId, userId);
        if (category == null) {
            throw new NotFoundException("Category not found!");
        }
        categoryRepository.deleteById(categoryId);
        return category;
    }

    public CategoryWithoutPlannedPaymentsDTO getById(int categoryId, int userId) {
        Optional<Category> optCategory = categoryRepository.findById(categoryId);
        if (optCategory.isEmpty()) {
            throw new NotFoundException("Category not found!");
        }
        if (optCategory.get().getOwner().getId() != userId) {
            throw new BadRequestException("Cannot show categories of other users!");
        }
        CategoryWithoutPlannedPaymentsDTO category = new CategoryWithoutPlannedPaymentsDTO(optCategory.get());
        return category;
    }

    public List<Category> getAll(int userId) {
        return categoryRepository.findAllByOwnerId(userId);
    }
}
