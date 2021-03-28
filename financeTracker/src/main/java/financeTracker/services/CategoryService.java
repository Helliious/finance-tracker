package financeTracker.services;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.category_dto.CategoryWithoutPlannedPaymentsDTO;
import financeTracker.models.pojo.Category;
import financeTracker.models.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category add(Category category) {
        Category responseCategory = categoryRepository.save(category);
        return responseCategory;
    }

    public Category delete(int categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new NotFoundException("Category not found!");
        }
        categoryRepository.deleteById(categoryId);
        return category.get();
    }

    public CategoryWithoutPlannedPaymentsDTO getById(int categoryId) {
        Optional<Category> optCategory = categoryRepository.findById(categoryId);
        if (optCategory.isEmpty()) {
            throw new NotFoundException("Category not found!");
        }
        CategoryWithoutPlannedPaymentsDTO category = new CategoryWithoutPlannedPaymentsDTO(optCategory.get());
        return category;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}
