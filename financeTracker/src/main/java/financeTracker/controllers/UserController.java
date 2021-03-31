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
        UserWithoutPassDTO responseUser = userService.deleteUser(userId);
        sessionManager.logoutUser(session);
        return responseUser;
    }

    @GetMapping("/users/logout")
    public UserWithoutPassDTO logout(HttpSession session) {
        int userId = sessionManager.validateSession(session);
        UserWithoutPassDTO responseUser = userService.logoutUser(userId);
        sessionManager.logoutUser(session);
        return responseUser;
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
