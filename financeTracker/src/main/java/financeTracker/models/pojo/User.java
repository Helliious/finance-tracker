package financeTracker.models.pojo;

import financeTracker.models.dto.user_dto.RegisterRequestUserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private Timestamp createTime;
    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    public User(RegisterRequestUserDTO userDTO) {
        firstName = userDTO.getFirstName();
        lastName = userDTO.getLastName();
        username = userDTO.getUsername();
        password = userDTO.getPassword();
        email = userDTO.getEmail();
        createTime = userDTO.getCreateTime();
        transactions = new ArrayList<>();
    }
}
