package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.models.dto.account_dto.CreateAccountDTO;
import financeTracker.models.dto.user_dto.RegisterRequestUserDTO;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private UserRepository userRepository;

    @Test(expected = BadRequestException.class)
    public void testAddAccountThrowsBadRequestExceptionBalanceBiggerThanAccLimit() {
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setAccLimit(100.0);
        createAccountDTO.setBalance(120.0);
        accountService.createAcc(createAccountDTO, 1);
    }
}
