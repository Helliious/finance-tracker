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
public class CategoryWithoutPlannedPaymentsDTO {
    private String name;
    private String type;

    public CategoryWithoutPlannedPaymentsDTO(Category category) {
        name = category.getName();
        type = category.getType();
    }
}
