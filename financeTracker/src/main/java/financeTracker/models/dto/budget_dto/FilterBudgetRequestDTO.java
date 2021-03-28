package financeTracker.models.dto.budget_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class FilterBudgetRequestDTO {
 private String name;
 private double amountFrom;
 private double amountTo;
 private int categoryId;

}
