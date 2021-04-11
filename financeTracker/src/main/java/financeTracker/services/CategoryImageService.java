package financeTracker.services;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.category_dto.CategoryImageDTO;
import financeTracker.models.pojo.Category;
import financeTracker.models.pojo.CategoryImage;
import financeTracker.models.repository.CategoryImageRepository;
import financeTracker.models.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class CategoryImageService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryImageRepository categoryImageRepository;

    public CategoryImageDTO upload(int id, File pFile) {
        CategoryImage categoryImage = new CategoryImage();
        categoryImage.setUrl(pFile.getAbsolutePath());
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw new NotFoundException("Category not found");
        }
        categoryImage.setCategory(category);
        categoryImageRepository.save(categoryImage);
        return new CategoryImageDTO(categoryImage);
    }

    public byte[] download(int id) throws IOException {
        Optional<CategoryImage> optCategoryImage = categoryImageRepository.findById(id);
        if (optCategoryImage.isEmpty()) {
            throw new NotFoundException("Image not found");
        }
        CategoryImage categoryImage = optCategoryImage.get();
        String url = categoryImage.getUrl();
        File pFile = new File(url);
        return Files.readAllBytes(pFile.toPath());
    }
}
