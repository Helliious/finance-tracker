package financeTracker.utils;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Category;
import financeTracker.models.pojo.User;

import java.util.Optional;

public class Validator {
    public static void validateData(Optional<Account> optAccount,
                              Optional<Category> optCategory,
                              Optional<User> optUser) {
        if (optAccount.isEmpty()) {
            throw new NotFoundException("Account not found!");
        }
        if (optCategory.isEmpty()) {
            throw new NotFoundException("Category not found!");
        }
        if (optUser.isEmpty()) {
            throw new NotFoundException("User not found!");
        }
    }
}
