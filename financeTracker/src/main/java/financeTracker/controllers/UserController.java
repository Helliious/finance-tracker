package financeTracker.controllers;

import financeTracker.models.dto.user_dto.*;
import financeTracker.models.pojo.User;
import financeTracker.services.UserService;
import financeTracker.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class UserController extends AbstractController {
    @Autowired
    private UserService userService;
    @Autowired
    private SessionManager sessionManager;

    @GetMapping("/users")
    public UserWithoutPassDTO getById(HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        User user = userService.getUserById(userId);
        return new UserWithoutPassDTO(user);
    }

    @PutMapping("/users")
    public UserWithoutPassDTO register(@RequestBody RegisterRequestUserDTO userDTO) {
        User user = userService.addUser(userDTO);
        return new UserWithoutPassDTO(user);
    }

    @PostMapping("/users")
    public UserWithoutPassDTO login(@RequestBody LoginUserDTO loginUserDto, HttpSession session) {
        User user = userService.login(loginUserDto);
        sessionManager.loginUser(session, user.getId());
        return new UserWithoutPassDTO(user);
    }

    @PutMapping("/users/edit")
    public UserWithoutPassDTO edit(@RequestBody UpdateRequestUserDTO userDTO,
                                   HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        User user = userService.editUser(userDTO, userId);
        return new UserWithoutPassDTO(user);
    }

    @DeleteMapping("/users")
    public UserWithoutPassDTO delete(HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        return userService.deleteUser(userId);
    }

    @GetMapping("/users/logout")
    public UserWithoutPassDTO logout(HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        User user = userService.logoutUser(userId);
        sessionManager.logoutUser(session);
        return new UserWithoutPassDTO(user);
    }

    @PostMapping("/users/change_password")
    public UserWithoutPassDTO changePassword(@RequestBody ChangePassUserDTO changePasswordDTO,
                                             HttpSession session) {
        int userId = sessionManager.getLoggedId(session);
        User user = userService.changePassword(userId, changePasswordDTO);
        return new UserWithoutPassDTO(user);
    }

    @PostMapping("/users/forgot_password")
    public ForgotPassMessageDTO forgotPassword(@RequestBody ForgotPassUserDTO forgotPassUserDto) {
        User user = userService.forgotPass(forgotPassUserDto.getEmail());
        return new ForgotPassMessageDTO(user);
    }
}
