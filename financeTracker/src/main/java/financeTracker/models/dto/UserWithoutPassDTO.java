package financeTracker.models.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class UserWithoutPassDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Timestamp createTime;
}
