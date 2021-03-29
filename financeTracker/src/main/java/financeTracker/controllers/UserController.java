package financeTracker.controllers;

import financeTracker.models.dto.user_dto.*;
import financeTracker.services.UserService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

@RestController
public class UserController extends AbstractController {
    @Autowired
    private UserService userService;
    @Autowired
    private SessionManager sessionManager;

    @GetMapping("/users/{id}")
    public UserWithoutPassDTO getById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/users")
    public UserWithoutPassDTO register(@RequestBody RegisterRequestUserDTO userDTO) {
        userDTO.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return userService.addUser(userDTO);
    }

    @PostMapping("/users")
    public UserWithoutPassDTO login(@RequestBody LoginUserDTO loginUserDto, HttpSession session) {
        UserWithoutPassDTO responseDto = userService.login(loginUserDto);
        sessionManager.loginUser(session, responseDto.getId());
        return responseDto;
    }

    @PutMapping("/users/edit")
    public UserWithoutPassDTO edit(@RequestBody UpdateRequestUserDTO userDTO,
                                   HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return userService.editUser(userDTO, userId);
    }

    @DeleteMapping("/users")
    public UserWithoutPassDTO delete(HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return userService.deleteUser(userId);
    }

    @GetMapping("/users/logout")
    public String logout(HttpSession session) {
        sessionManager.validateSession(session);
        sessionManager.logoutUser(session);
        return "Logged out!";
    }

    @PostMapping("/users/change_password")
    public UserWithoutPassDTO changePassword(@RequestBody ChangePassUserDTO changePasswordDTO,
                                             HttpSession session) {
        int userId = sessionManager.validateSession(session);
        return userService.changePassword(userId, changePasswordDTO);
    }

    @PostMapping("/users/forgot_password")
    public UserWithoutPassDTO forgotPassword(@RequestBody ForgotPassUserDTO forgotPassUserDto) {
        return userService.forgotPass(forgotPassUserDto.getEmail());
    }
}
