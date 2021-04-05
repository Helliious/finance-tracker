package financeTracker.services;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.user_dto.LoginUserDTO;
import financeTracker.models.dto.user_dto.RegisterRequestUserDTO;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Optional;


@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CategoryRepository categoryRepository;

    @Test(expected = BadRequestException.class)
    public void testAddUserThrowsBadRequestExceptionWhenUserAlreadyExists() {
        String name = "dummy";
        Mockito.when(userRepository.findByUsername(name))
                .thenReturn(new User());
        userService.addUser(new RegisterRequestUserDTO(
                "test",
                "test",
                name,
                "tesT_1",
                "tesT_1",
                "test@abv.bg",
                null
        ));
    }

    @Test(expected = BadRequestException.class)
    public void testAddUserThrowsBadRequestExceptionWhenEmailExists() {
        String email = "dummy@gmail.com";
        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(new User());
        userService.addUser(new RegisterRequestUserDTO(
                "test",
                "test",
                "test",
                "tesT_1",
                "tesT_1",
                email,
                null
        ));
    }

    @Test(expected = BadRequestException.class)
    public void testAddUserThrowsBadRequestExceptionWhenPassNotMatch() {
        userService.addUser(new RegisterRequestUserDTO(
                "test",
                "test",
                "test",
                "tesT_1",
                "tesT_2",
                "test@abv.bg",
                null
        ));
    }

    @Test
    public void testAddUserSuccess() {
        RegisterRequestUserDTO regUser = new RegisterRequestUserDTO(
                "test",
                "test",
                "dummy",
                "tesT_1",
                "tesT_1",
                "dummy@abv.bg",
                null
        );
        User user = new User(regUser);
        Mockito.when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(null);
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(null);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(categoryRepository.findAllByOwnerIsNull()).thenReturn(new ArrayList<>());
        User resultUser = userService.addUser(regUser);

        assert(user.getUsername().equals(resultUser.getUsername()));
        assert(user.getEmail().equals(resultUser.getEmail()));
        assert(user.getPassword().equals(resultUser.getPassword()));
        assert(user.getId() == resultUser.getId());
        assert(user.getFirstName().equals(resultUser.getFirstName()));
        assert(user.getLastName().equals(resultUser.getLastName()));
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserByIdThrowsNotFoundExceptionWhenUserIsNull() {
        int userId = 1;
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        userService.getUserById(userId);
    }

    @Test
    public void testGetUserByIdSuccess() {
        int userId = 1;
        User user = new User(new RegisterRequestUserDTO(
                "test",
                "test",
                "dummy",
                "tesT_1",
                "tesT_1",
                "dummy@abv.bg",
                null
        ));
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        User resultUser = userService.getUserById(userId);

        assert(user.getUsername().equals(resultUser.getUsername()));
        assert(user.getEmail().equals(resultUser.getEmail()));
        assert(user.getPassword().equals(resultUser.getPassword()));
        assert(user.getId() == resultUser.getId());
        assert(user.getFirstName().equals(resultUser.getFirstName()));
        assert(user.getLastName().equals(resultUser.getLastName()));
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginThrowsAuthenticationExceptionWhenUserIsNull() {
        String username = "test";
        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(null);
        userService.login(new LoginUserDTO());
    }

    @Test(expected = AuthenticationException.class)
    public void testLoginThrowsNotFoundExceptionWhenPassNotMatch() {
        LoginUserDTO loginUserDTO = new LoginUserDTO(
                "test",
                "tesT_1"
        );
        Mockito.when(userRepository.findByUsername(loginUserDTO.getUsername()))
                .thenReturn(new User());
        userService.login(loginUserDTO);
    }
}
