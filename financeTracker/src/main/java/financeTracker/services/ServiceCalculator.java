package financeTracker.services;

import financeTracker.models.pojo.Account;
import financeTracker.utils.Action;
import financeTracker.utils.Constants;

public class ServiceCalculator {
    static void calculateBalance(double amount, String paymentType, Account account, Action action) {
        if (action == Action.ADD) {
            if (paymentType.equals(Constants.INCOME)) {
                account.increaseBalance(amount);
            } else {
                account.reduceBalance(amount);
            }
        } else {
            if (paymentType.equals(Constants.INCOME)) {
                account.reduceBalance(amount);
            } else {
                account.increaseBalance(amount);
            }
        }
    }
}
