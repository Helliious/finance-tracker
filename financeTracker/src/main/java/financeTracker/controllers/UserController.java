package financeTracker.controllers;

import financeTracker.models.dto.user_dto.*;
import financeTracker.services.UserService;
import financeTracker.utils.SessionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

@RestController
public class UserController extends AbstractController {
    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    public UserWithoutPassDTO getById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/users/register")
    public UserWithoutPassDTO register(@RequestBody RegisterRequestUserDTO userDTO) {
        userDTO.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return userService.addUser(userDTO);
    }

    @PostMapping("/users/login")
    public UserWithoutPassDTO login(@RequestBody LoginUserDTO loginUserDto, HttpSession session) {
        UserWithoutPassDTO responseDto = userService.login(loginUserDto);
        session.setAttribute("LoggedUser", responseDto.getId());
        return responseDto;
    }

    @PutMapping("/users/{user_id}/edit")
    public UserWithoutPassDTO edit(@PathVariable(name = "user_id") int id,
                                   @RequestBody UpdateRequestUserDTO userDTO,
                                   HttpSession session) {
        String message = "Cannot modify other users!";
        SessionValidator.validateSession(session, message, id);
        return userService.editUser(userDTO, id);
    }

    @DeleteMapping("/users/{user_id}/delete")
    public UserWithoutPassDTO delete(@PathVariable(name = "user_id") int id, HttpSession session) {
        String message = "Cannot delete other users!";
        SessionValidator.validateSession(session, message, id);
        return userService.deleteUser(id);
    }

    @PostMapping("/users/{id}")
    public String logout(@PathVariable int id, HttpSession session) {
        String message = "Cannot logout other users!";
        SessionValidator.validateSession(session, message, id);
        session.setAttribute("LoggedUser", null);
        return "Logged out!";
    }

    @PostMapping("/users/forgot_password")
    public UserWithoutPassDTO forgotPassword(@RequestBody ForgotPassUserDTO forgotPassUserDto) {
        return userService.forgotPass(forgotPassUserDto.getEmail());
    }
}
