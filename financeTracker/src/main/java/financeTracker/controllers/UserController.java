package financeTracker.controllers;

import financeTracker.models.users.LoginUserDto;
import financeTracker.models.users.User;
import financeTracker.models.users.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserDao userDao;

    @GetMapping("/users/{id}")
    public User getById(@PathVariable int id) {
        User user = userDao.getById(id);
        return user;
    }

    @PostMapping("/login")
    public String userLogin(@RequestBody LoginUserDto loginUserDto) {
        return ("Loged in with username: " + loginUserDto.getUsername() + ", password: " + loginUserDto.getPassword());
    }
}
