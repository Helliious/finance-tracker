package financeTracker.models.dto.account_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class FilterAccountRequestDTO {
    private String name;
    private Double balanceFrom;
    private Double balanceTo;
    private Double accLimitFrom;
    private Double accLimitTo;
    private Timestamp createTimeFrom;
    private Timestamp createTimeTo;
}
