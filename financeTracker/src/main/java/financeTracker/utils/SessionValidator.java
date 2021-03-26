package financeTracker.utils;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;

import javax.servlet.http.HttpSession;

public class SessionValidator {
    public static void validateSession(HttpSession session, String message, int ownerId){
        if (session.getAttribute("LoggedUser") == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            int loggedId = (int) session.getAttribute("LoggedUser");
            if (loggedId != ownerId) {
                throw new BadRequestException(message);
            }
        }
    }
}
