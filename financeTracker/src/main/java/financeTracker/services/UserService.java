package financeTracker.services;

import financeTracker.email.EmailServiceImpl;
import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.user_dto.*;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.UserRepository;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EmailServiceImpl emailService;

    public User addUser(RegisterRequestUserDTO userDTO) {
        userDTO.setCreateTime(new Timestamp(System.currentTimeMillis()));
        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("Email already exists");
        }
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new BadRequestException("Confirm password doesn't match");
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        User user = new User(userDTO);
        user.getCategories().addAll(categoryRepository.findAllByOwnerIsNull());
        user = userRepository.save(user);
        return user;
    }

    public User editUser(UpdateRequestUserDTO userDTO, int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        if (userDTO.getUsername() != null) {
            if (userRepository.findByUsername(userDTO.getUsername()) != null) {
                throw new BadRequestException("Username already exists");
            }
            user.get().setUsername(userDTO.getUsername());
        }
        if (userDTO.getFirstName() != null) {
            user.get().setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.get().setLastName(userDTO.getLastName());
        }
        if (userDTO.getEmail() != null) {
            if (userRepository.findByEmail(userDTO.getEmail()) != null) {
                throw new BadRequestException("Email already exists");
            }
            user.get().setEmail(userDTO.getEmail());
        }
        User responseUser = userRepository.save(user.get());
        responseUser.getCategories().addAll(categoryRepository.findAllByOwnerIsNull());
        return responseUser;
    }

    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().getCategories().addAll(categoryRepository.findAllByOwnerIsNull());
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
                user.getCategories().addAll(categoryRepository.findAllByOwnerIsNull());
                return user;
            } else {
                throw new AuthenticationException("Wrong credentials");
            }
        }
    }

    public UserWithoutPassDTO deleteUser(int userId) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = optUser.get();
        user.getCategories().addAll(categoryRepository.findAllByOwnerIsNull());
        //cannot map entity after it is deleted
        UserWithoutPassDTO responseUser = new UserWithoutPassDTO(user);
        userRepository.delete(user);
        return responseUser;
    }

    public User forgotPass(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        List<CharacterRule> characterRules = new ArrayList<>();
        characterRules.add(new CharacterRule(EnglishCharacterData.Digit));
        characterRules.add(new CharacterRule(EnglishCharacterData.Alphabetical));
        characterRules.add(new CharacterRule(EnglishCharacterData.Special));
        PasswordGenerator passwordGenerator = new PasswordGenerator();
        String password = passwordGenerator.generatePassword(15, characterRules);
        String mailMessage = "New password: " + password;
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        emailService.sendPassword(email, "Forgot password", mailMessage);
        return user;
    }

    public User changePassword(int userId, ChangePassUserDTO changePasswordDTO) {
        Optional<User> optUser = userRepository.findById(userId);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        if (!encoder.matches(changePasswordDTO.getCurrentPassword(), optUser.get().getPassword())) {
            throw new AuthenticationException("Invalid current password");
        }
        if (!changePasswordDTO.getPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new AuthenticationException("Confirm password doesn't match");
        }
        if (encoder.matches(changePasswordDTO.getPassword(), optUser.get().getPassword())) {
            throw new AuthenticationException("Entered the same password as current one");
        } else {
            User user = optUser.get();
            user.setPassword(encoder.encode(changePasswordDTO.getPassword()));
            userRepository.save(user);
            user.getCategories().addAll(categoryRepository.findAllByOwnerIsNull());
            return user;
        }
    }

    public User logoutUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        user.get().getCategories().addAll(categoryRepository.findAllByOwnerIsNull());
        return user.get();
    }
}
