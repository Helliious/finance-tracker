package financeTracker.models.dto.user_dto;


import financeTracker.models.dto.account_dto.AccountWithoutOwnerDTO;
import financeTracker.models.dto.category_dto.ResponseCategoryDTO;
import financeTracker.models.pojo.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Component
public class UserWithoutPassDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Timestamp createTime;
    private List<AccountWithoutOwnerDTO> accounts;
    private List<ResponseCategoryDTO> categories;

    public UserWithoutPassDTO(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        username = user.getUsername();
        email = user.getEmail();
        createTime = user.getCreateTime();
        accounts = new ArrayList<>();
        if (user.getAccounts() != null) {
            for (Account a : user.getAccounts()) {
                accounts.add(new AccountWithoutOwnerDTO(a));
            }
        }
        categories = new ArrayList<>();
        if (user.getCategories() != null) {
            for (Category c : user.getCategories()) {
                categories.add(new ResponseCategoryDTO(c));
            }
        }
    }
}
