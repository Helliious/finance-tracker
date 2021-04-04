package financeTracker.services;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dao.CategoryDAO;
import financeTracker.models.dto.category_dto.AddCategoryDTO;
import financeTracker.models.dto.category_dto.CategoryExpensesDTO;
import financeTracker.models.pojo.Category;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.UserRepository;
import financeTracker.utils.PDFCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    public Category add(AddCategoryDTO category, int userId) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw new AuthenticationException("User not found!");
        }
        Category result = new Category();
        result.setName(category.getName());
        result.setType(category.getType());
        result.setOwner(optUser.get());
        return categoryRepository.save(result);
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
            category = categoryRepository.findByIdAndOwnerIsNull(categoryId);
            if (category == null) {
                throw new NotFoundException("Category not found!");
            }
        }
        return category;
    }

    public List<Category> getAll(int userId) {
        List<Category> categories = categoryRepository.findAllByOwnerId(userId);
        categories.addAll(categoryRepository.findAllByOwnerIsNull());
        return categories;
    }

    @Scheduled(fixedRate = 10000)
    public void referenceOverallExpensesByCategory() {
        List<CategoryExpensesDTO> expenses = categoryDAO.referenceOverallExpensesByCategory();
        if (!expenses.isEmpty()) {
            String currUsername = expenses.get(0).getUsername();
            StringBuilder text = new StringBuilder();
            for (CategoryExpensesDTO e : expenses) {
                if (!currUsername.equals(e.getUsername())) {
                    pdfCreator.insertTextInPDF(text.toString(), currUsername);
                    currUsername = e.getUsername();
                    text.setLength(0);
                }
                text.append(e.getName()).append(" : ").append(e.getExpenses()).append(";\n");
            }
            pdfCreator.insertTextInPDF(text.toString(), currUsername);
        }
    }
}
