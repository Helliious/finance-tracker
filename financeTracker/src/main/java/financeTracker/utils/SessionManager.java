package financeTracker.utils;

import financeTracker.exceptions.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class SessionManager {
    public static final String LOGGED_USER_ID = "LOGGED_USER_ID";

    public int validateSession(HttpSession session){
        if (session.getAttribute(LOGGED_USER_ID) == null) {
            throw new AuthenticationException("Not logged in!");
        } else {
            int loggedId = (int) session.getAttribute(LOGGED_USER_ID);
            return loggedId;
        }
    }

    public void loginUser(HttpSession session, int userId) {
        session.setAttribute(LOGGED_USER_ID, userId);
    }

    public void logoutUser(HttpSession session) {
        session.invalidate();
    }
}
