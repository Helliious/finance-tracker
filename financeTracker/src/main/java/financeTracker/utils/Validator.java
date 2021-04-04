package financeTracker.utils;

import financeTracker.exceptions.NotFoundException;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Category;

public class Validator {
    public static void validateData(Account account,
                                    Category category) {
        if (account == null) {
            throw new NotFoundException("Account not found!");
        }
        if (category == null) {
            throw new NotFoundException("Category not found!");
        }
    }
}
