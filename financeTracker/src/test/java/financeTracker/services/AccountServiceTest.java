package financeTracker.services;

import financeTracker.exceptions.BadRequestException;
import financeTracker.models.dto.account_dto.CreateAccountDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test(expected = BadRequestException.class)
    public void testAddAccountThrowsBadRequestExceptionBalanceBiggerThanAccLimit() {
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setAccLimit(100.0);
        createAccountDTO.setBalance(120.0);
        accountService.createAcc(createAccountDTO, 1);
    }
}
