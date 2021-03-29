package financeTracker.utils;

import financeTracker.exceptions.AuthenticationException;
import financeTracker.exceptions.BadRequestException;

import javax.servlet.http.HttpSession;

public class SessionManager {
    public static final String LOGGED_USER_ID = "LOGGED_USER_ID";

    public static int validateSession(HttpSession session, String message, int ownerId){
        int loggedId;
        if (session.getAttribute(LOGGED_USER_ID) == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            loggedId = (int) session.getAttribute(LOGGED_USER_ID);
            if (loggedId != ownerId) {
                throw new BadRequestException(message);
            }
        }
        return loggedId;
    }
}
