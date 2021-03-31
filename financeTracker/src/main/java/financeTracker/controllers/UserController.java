package financeTracker.controllers;

import financeTracker.models.dto.user_dto.*;
import financeTracker.models.pojo.User;
import financeTracker.services.UserService;
import financeTracker.utils.SessionManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class UserController extends AbstractController {
    @Autowired
    private UserService userService;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/users/{id}")
    public UserWithoutPassDTO getById(@PathVariable int id) {
        User user = userService.getUserById(id);
        return convertToUserWithoutPassDTO(user);
    }

    @PutMapping("/users")
    public UserWithoutPassDTO register(@RequestBody RegisterRequestUserDTO userDTO) {
        User user = userService.addUser(userDTO);
        return convertToUserWithoutPassDTO(user);
    }

    @PostMapping("/users")
    public UserWithoutPassDTO login(@RequestBody LoginUserDTO loginUserDto, HttpSession session) {
        User user = userService.login(loginUserDto);
        sessionManager.loginUser(session, user.getId());
        return convertToUserWithoutPassDTO(user);
    }

    @PutMapping("/users/edit")
    public UserWithoutPassDTO edit(@RequestBody UpdateRequestUserDTO userDTO,
                                   HttpSession session) {
        int userId = sessionManager.validateSession(session);
        User user = userService.editUser(userDTO, userId);
        return convertToUserWithoutPassDTO(user);
    }

    @DeleteMapping("/users")
    public UserWithoutPassDTO delete(HttpSession session) {
        int userId = sessionManager.validateSession(session);
        User user = userService.deleteUser(userId);
        sessionManager.logoutUser(session);
        return convertToUserWithoutPassDTO(user);
    }

    @GetMapping("/users/logout")
    public UserWithoutPassDTO logout(HttpSession session) {
        int userId = sessionManager.validateSession(session);
        User user = userService.logoutUser(userId);
        sessionManager.logoutUser(session);
        return convertToUserWithoutPassDTO(user);
    }

    @PostMapping("/users/change_password")
    public UserWithoutPassDTO changePassword(@RequestBody ChangePassUserDTO changePasswordDTO,
                                             HttpSession session) {
        int userId = sessionManager.validateSession(session);
        User user = userService.changePassword(userId, changePasswordDTO);
        return convertToUserWithoutPassDTO(user);
    }

    @PostMapping("/users/forgot_password")
    public UserWithoutPassDTO forgotPassword(@RequestBody ForgotPassUserDTO forgotPassUserDto) {
        User user = userService.forgotPass(forgotPassUserDto.getEmail());
        return convertToUserWithoutPassDTO(user);
    }

    private UserWithoutPassDTO convertToUserWithoutPassDTO(User user) {
        return modelMapper.map(user, UserWithoutPassDTO.class);
    }
}
