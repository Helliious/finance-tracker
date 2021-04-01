package financeTracker.services;

import financeTracker.email.EmailServiceImpl;
import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.user_dto.*;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailServiceImpl emailService;

    public User addUser(RegisterRequestUserDTO userDTO) {
        userDTO.setCreateTime(new Timestamp(System.currentTimeMillis()));
        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            throw new BadRequestException("Username already exists!");
        }
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("Email already exists!");
        }
        validateUser(userDTO);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        User user = new User(userDTO);
        user = userRepository.save(user);
        return user;
    }

    public User editUser(UpdateRequestUserDTO userDTO, int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        if (userDTO.getUsername() != null) {
            if (user.get().getUsername().equals(userDTO.getUsername())) {
                throw new BadRequestException("Entered the same username!");
            } else if (userRepository.findByUsername(userDTO.getUsername()) != null) {
                throw new BadRequestException("Username already exists!");
            } else {
                user.get().setUsername(userDTO.getUsername());
            }
        }
        if (userDTO.getFirstName() != null) {
            if (user.get().getFirstName().equals(userDTO.getFirstName())) {
                throw new BadRequestException("Entered the same first name!");
            } else {
                user.get().setFirstName(userDTO.getFirstName());
            }
        }
        if (userDTO.getLastName() != null) {
            if (user.get().getLastName().equals(userDTO.getLastName())) {
                throw new BadRequestException("Entered the same last name!");
            } else {
                user.get().setLastName(userDTO.getLastName());
            }
        }
        if (userDTO.getEmail() != null) {
            if (user.get().getEmail().equals(userDTO.getEmail())) {
                throw new BadRequestException("Entered the same email!");
            } else if (userRepository.findByEmail(userDTO.getEmail()) != null) {
                throw new BadRequestException("Email already exists!");
            } else {
                user.get().setEmail(userDTO.getEmail());
            }
        }
        User responseUser = userRepository.save(user.get());
        return responseUser;
    }

    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public User login(LoginUserDTO loginUserDto) {
        User user = userRepository.findByUsername(loginUserDto.getUsername());
        if (user == null) {
            throw new AuthenticationException("Wrong credentials");
        } else {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(loginUserDto.getPassword(), user.getPassword())) {
                return user;
            } else {
                throw new AuthenticationException("Wrong credentials");
            }
        }
    }

    public User deleteUser(int userId) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        userRepository.deleteById(userId);
        return optUser.get();
    }

    public User forgotPass(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
        String generatedPass = RandomString.make(20);
        String mailMessage = "New password: " + generatedPass;
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(generatedPass));
        userRepository.save(user);
        emailService.sendPassword(email, "Forgot password", mailMessage);
        return user;
    }

    public User changePassword(int userId, ChangePassUserDTO changePasswordDTO) {
        Optional<User> optUser = userRepository.findById(userId);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        if (!encoder.matches(changePasswordDTO.getCurrentPassword(), optUser.get().getPassword())) {
            throw new AuthenticationException("Invalid current password!");
        }
        if (!changePasswordDTO.getPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new AuthenticationException("Confirm password doesn't match!");
        }
        if (encoder.matches(changePasswordDTO.getPassword(), optUser.get().getPassword())) {
            throw new AuthenticationException("Entered the same password as current one!");
        } else {
            optUser.get().setPassword(encoder.encode(changePasswordDTO.getPassword()));
            userRepository.save(optUser.get());
            return optUser.get();
        }
    }

    public User logoutUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        return user.get();
    }

    private void validateUser(RegisterRequestUserDTO userDTO) {
        if (userDTO.getFirstName() == null) {
            throw new BadRequestException("Must enter valid first name!");
        }
        if (userDTO.getLastName() == null) {
            throw new BadRequestException("Must enter valid last name!");
        }
        if (userDTO.getUsername() == null) {
            throw new BadRequestException("Must enter valid username!");
        }
        if (userDTO.getPassword() == null) {
            throw new BadRequestException("Must enter valid password!");
        }
        if (userDTO.getEmail() == null) {
            throw new BadRequestException("Must enter valid email!");
        }
    }
}
