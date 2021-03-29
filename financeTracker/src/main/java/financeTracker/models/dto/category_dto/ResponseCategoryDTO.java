package financeTracker.models.dto.category_dto;

import financeTracker.models.pojo.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class ResponseCategoryDTO {
    private int id;
    private String name;
    private String type;
    private CategoryImageDTO imageDTO;

    public ResponseCategoryDTO(Category category) {
        id = category.getId();
        name = category.getName();
        type = category.getType();
        if (category.getImage() != null) {
            imageDTO = new CategoryImageDTO(category.getImage());
        }
    }
}
