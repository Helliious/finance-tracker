package financeTracker.models.dto.category_dto;

import financeTracker.models.pojo.CategoryImage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class CategoryImageDTO {
    private int id;
    private String url;

    public CategoryImageDTO(CategoryImage categoryImage) {
        id = categoryImage.getId();
        url = categoryImage.getUrl();
    }
}
