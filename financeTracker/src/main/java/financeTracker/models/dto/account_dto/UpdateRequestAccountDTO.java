package financeTracker.models.dto.account_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class UpdateRequestAccountDTO {
    private String name;
    private double balance;
    private double accLimit;
}
