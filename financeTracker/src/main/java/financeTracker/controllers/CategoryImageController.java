package financeTracker.controllers;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.category_dto.CategoryImageDTO;
import financeTracker.services.CategoryImageService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;

@RestController
public class CategoryImageController extends AbstractController {
    @Value("${file.path}")
    private String filePath;
    @Autowired
    private CategoryImageService categoryImageService;
    @Autowired
    private SessionManager sessionManager;

    @PutMapping("/categories/{id}/images")
    public CategoryImageDTO upload(@PathVariable int id,
                                   @RequestPart MultipartFile file,
                                   HttpSession session) {
        sessionManager.getLoggedId(session);
        File pFile = new File(filePath + File.separator + System.nanoTime() + ".png");
        try (OutputStream os = new FileOutputStream(pFile)) {
            os.write(file.getBytes());
            return categoryImageService.upload(id, pFile);
        } catch (FileNotFoundException e) {
            throw new NotFoundException("File not found, reason - " + e.getMessage());
        } catch (IOException e) {
            throw new BadRequestException("I/O exception, reason - " + e.getMessage());
        }
    }

    @GetMapping(value = "images/{id}", produces = "image/*")
    public byte[] download(@PathVariable int id,
                           HttpSession session) {
        sessionManager.getLoggedId(session);
        try {
            return categoryImageService.download(id);
        } catch (IOException e) {
            throw new BadRequestException("I/O exception, reason - " + e.getMessage());
        }
    }
}
