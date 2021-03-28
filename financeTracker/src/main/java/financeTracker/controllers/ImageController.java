package financeTracker.controllers;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.category_dto.CategoryImageDTO;
import financeTracker.models.pojo.CategoryImage;
import financeTracker.models.repository.CategoryImageRepository;
import financeTracker.models.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

@RestController
public class ImageController {
    @Value("${file.path}")
    private String filePath;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryImageRepository categoryImageRepository;

    @PutMapping("/category/{id}/images")
    public CategoryImageDTO upload(@PathVariable int id, @RequestPart MultipartFile file) {
        File pFile = new File(filePath + File.separator + System.nanoTime() + ".png");
        try (OutputStream os = new FileOutputStream(pFile)) {
            os.write(file.getBytes());
            CategoryImage categoryImage = new CategoryImage();
            categoryImage.setUrl(pFile.getAbsolutePath());
            categoryImage.setCategory(categoryRepository.findById(id).get());
            categoryImageRepository.save(categoryImage);
            return new CategoryImageDTO(categoryImage);
        } catch (FileNotFoundException e) {
            throw new NotFoundException("File not found, reason - " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping(value = "images/{id}", produces = "image/*")
    public byte[] download(@PathVariable int id) throws IOException {
        CategoryImage categoryImage = categoryImageRepository.findById(id).get();
        String url = categoryImage.getUrl();
        File pFile = new File(url);
        return Files.readAllBytes(pFile.toPath());
    }
}
