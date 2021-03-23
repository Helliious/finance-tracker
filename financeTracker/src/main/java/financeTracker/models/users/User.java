package financeTracker.models.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private Timestamp createTime;
}
