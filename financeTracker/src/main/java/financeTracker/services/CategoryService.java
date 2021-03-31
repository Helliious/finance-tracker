package financeTracker.services;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.CategoryDAO;
import financeTracker.models.dto.category_dto.CategoryExpensesDTO;
import financeTracker.models.pojo.Category;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.UserRepository;
import financeTracker.utils.PDFCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PDFCreator pdfCreator;

    public Category add(Category category, int userId) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw new AuthenticationException("User not found!");
        }
        validateCategory(category);
        category.setOwner(optUser.get());
        Category result = categoryRepository.save(category);
        return result;
    }

    public Category delete(int categoryId, int userId) {
        Category category = categoryRepository.findByIdAndOwnerId(categoryId, userId);
        if (category == null) {
            throw new NotFoundException("Category not found!");
        }
        categoryRepository.deleteById(categoryId);
        return category;
    }

    public Category getById(int categoryId, int userId) {
        Category category = categoryRepository.findByIdAndOwnerId(categoryId, userId);
        if (category == null) {
            throw new NotFoundException("Category not found!");
        }
        return category;
    }

    public List<Category> getAll(int userId) {
        return categoryRepository.findAllByOwnerId(userId);
    }

    private void validateCategory(Category category) {
        if (category == null) {
            throw new BadRequestException("Wrong category credentials!");
        }
        if (category.getType() == null) {
            throw new BadRequestException("Need to enter valid category type!");
        }
        if (category.getName() == null) {
            throw new BadRequestException("Need to enter valid category name!");
        }
    }

    public List<CategoryExpensesDTO> referenceOverallExpensesByCategory(int userId) {
        List<CategoryExpensesDTO> expenses = categoryDAO.referenceOverallExpensesByCategory(userId);
        StringBuilder text = new StringBuilder();
        for (CategoryExpensesDTO e : expenses) {
            text.append(e.getName()).append(" : ").append(e.getExpenses()).append(";\n");
        }
        pdfCreator.insertTextInPDF(text.toString(), userId);
        return expenses;
    }
}
