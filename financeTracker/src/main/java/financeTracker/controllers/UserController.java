package financeTracker.controllers;

import financeTracker.models.dto.LoginUserDto;
import financeTracker.models.pojo.User;
import financeTracker.models.dao.UserDao;
import financeTracker.models.dao.UserWithoutPassDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController extends AbstractController {
    @Autowired
    private UserDao userDao;

    @GetMapping("/users/{id}")
    public UserWithoutPassDto getById(@PathVariable int id) throws Exception {
        User user = userDao.getById(id);
        UserWithoutPassDto userWithoutPass = new UserWithoutPassDto();
        userWithoutPass.setId(user.getId());
        userWithoutPass.setUsername(user.getUsername());
        userWithoutPass.setEmail(user.getEmail());
        userWithoutPass.setFirstName(user.getFirstName());
        userWithoutPass.setLastName(user.getLastName());
        userWithoutPass.setCreateTime(user.getCreateTime());
        return userWithoutPass;
    }

    @PostMapping("/login")
    public String userLogin(@RequestBody LoginUserDto loginUserDto) {
        return ("Loged in with username: " + loginUserDto.getUsername() + ", password: " + loginUserDto.getPassword());
    }

    @GetMapping("/logout")
    public String userLogout() {
        return ("User logged out");
    }

}
