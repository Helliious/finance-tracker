package financeTracker.models.dto.category_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Component
public class AddCategoryDTO {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "Type cannot be empty")
    private String type;
}
