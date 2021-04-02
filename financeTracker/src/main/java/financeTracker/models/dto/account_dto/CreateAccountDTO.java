package financeTracker.models.dto.account_dto;

import financeTracker.models.pojo.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Component
public class CreateAccountDTO {
    private String name;
    private Double balance;
    private Double accLimit;
    private Timestamp createTime;
    private User owner;
}
