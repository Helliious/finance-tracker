package financeTracker.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import financeTracker.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Component
public class PDFCreator {
    @Value("${file.path.pdf}")
    private String filePath;

    public void insertTextInPDF(String text, int userId) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath + File.separator + "User_" + userId + ".pdf"));
            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Chunk chunk = new Chunk(text, font);
            document.add(chunk);
            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            throw new BadRequestException("I/O exception, reason - " + e.getMessage());
        }
    }
}
