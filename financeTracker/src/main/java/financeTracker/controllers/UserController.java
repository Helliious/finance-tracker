package financeTracker.controllers;

import financeTracker.models.dto.LoginUserDTO;
import financeTracker.models.pojo.User;
import financeTracker.models.dao.UserDAO;
import financeTracker.models.dto.UserWithoutPassDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController extends AbstractController {
    @Autowired
    private UserDAO userDao;

    @GetMapping("/users/{id}")
    public UserWithoutPassDTO getById(@PathVariable int id) throws Exception {
        User user = userDao.getById(id);
        UserWithoutPassDTO userWithoutPass = new UserWithoutPassDTO();
        userWithoutPass.setId(user.getId());
        userWithoutPass.setUsername(user.getUsername());
        userWithoutPass.setEmail(user.getEmail());
        userWithoutPass.setFirstName(user.getFirstName());
        userWithoutPass.setLastName(user.getLastName());
        userWithoutPass.setCreateTime(user.getCreateTime());
        return userWithoutPass;
    }

    @PostMapping("/login")
    public String userLogin(@RequestBody LoginUserDTO loginUserDto) {
        return ("Loged in with username: " + loginUserDto.getUsername() + ", password: " + loginUserDto.getPassword());
    }

    @GetMapping("/logout")
    public String userLogout() {
        return ("User logged out");
    }

}
