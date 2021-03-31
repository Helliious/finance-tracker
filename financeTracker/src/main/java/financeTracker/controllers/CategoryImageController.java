package financeTracker.controllers;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.category_dto.CategoryImageDTO;
import financeTracker.services.CategoryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
public class CategoryImageController {
    @Value("${file.path}")
    private String filePath;
    @Autowired
    private CategoryImageService categoryImageService;

    @PutMapping("/categories/{id}/images")
    public CategoryImageDTO upload(@PathVariable int id, @RequestPart MultipartFile file) {
        File pFile = new File(filePath + File.separator + System.nanoTime() + ".png");
        try (OutputStream os = new FileOutputStream(pFile)) {
            os.write(file.getBytes());
            return categoryImageService.upload(id, pFile);
        } catch (FileNotFoundException e) {
            throw new NotFoundException("File not found, reason - " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping(value = "images/{id}", produces = "image/*")
    public byte[] download(@PathVariable int id) throws IOException {
        return categoryImageService.download(id);
    }
}
