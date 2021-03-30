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

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailServiceImpl emailService;

    public UserWithoutPassDTO addUser(RegisterRequestUserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            throw new BadRequestException("Username already exists!");
        }
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("Email already exists!");
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        User user = new User(userDTO);
        user = userRepository.save(user);
        UserWithoutPassDTO userWithoutPassDTO = new UserWithoutPassDTO(user);
        return userWithoutPassDTO;
    }

    public UserWithoutPassDTO editUser(UpdateRequestUserDTO userDTO, int userId) {
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
        UserWithoutPassDTO userWithoutPassDTO = new UserWithoutPassDTO(responseUser);
        return userWithoutPassDTO;
    }

    public UserWithoutPassDTO getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return new UserWithoutPassDTO(user.get());
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public UserWithoutPassDTO login(LoginUserDTO loginUserDto) {
        User user = userRepository.findByUsername(loginUserDto.getUsername());
        if (user == null) {
            throw new AuthenticationException("Wrong credentials");
        } else {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(loginUserDto.getPassword(), user.getPassword())) {
                return new UserWithoutPassDTO(user);
            } else {
                throw new AuthenticationException("Wrong credentials");
            }
        }
    }

    public UserWithoutPassDTO deleteUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        UserWithoutPassDTO responseUser = new UserWithoutPassDTO(user.get());
        userRepository.deleteById(userId);
        return responseUser;
    }

    public UserWithoutPassDTO forgotPass(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
        String generatedPass = RandomString.make(20);
        String mailMessage = "New password: " + generatedPass;
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(generatedPass));
        UserWithoutPassDTO responseUser = new UserWithoutPassDTO(user);
        userRepository.save(user);
        emailService.sendPassword(email, "Forgot password", mailMessage);
        return responseUser;
    }

    public UserWithoutPassDTO changePassword(int userId, ChangePassUserDTO changePasswordDTO) {
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
        } else if (userRepository.findByPassword(changePasswordDTO.getPassword()) != null) {
            throw new AuthenticationException("Password already exists!");
        } else {
            optUser.get().setPassword(encoder.encode(changePasswordDTO.getPassword()));
            UserWithoutPassDTO user = new UserWithoutPassDTO(optUser.get());
            userRepository.save(optUser.get());
            return user;
        }
    }

    public UserWithoutPassDTO logoutUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
        return new UserWithoutPassDTO(user.get());
    }
}
