package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.handlers.AuthHandler;
import edu.byu.cs.tweeter.client.model.service.handlers.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.handlers.NoDataReturnedHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service {
    public static final String LOGIN_URL_PATH = "auth/login";
    public static final String REGISTER_URL_PATH = "auth/register";
    public static final String LOGOUT_URL_PATH = "logout";
    public static final String GET_USER_URL_PATH = "get/user";


    public interface GetUserObserver extends Observer {
        void getUserSuccess(User user);
    }

    public void login(String username, String password, AuthObserver observer) {
        // Send the login request.
        LoginTask loginTask = new LoginTask(username, password, new LoginHandler(observer));
        executeTask(loginTask);
    }
    public void register(String firstName, String lastName, String username, String password, String imageBytes, AuthObserver observer) {
        // Send the register request.
        RegisterTask registerTask = new RegisterTask(firstName, lastName, username, password, imageBytes, new RegisterHandler(observer));
        executeTask(registerTask);
    }
    public void logout(AuthToken authToken, NoDataReturnedObserver observer) {
        // Send the logout request.
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));
        executeTask(logoutTask);
    }
    public void getUser(AuthToken authToken, String username, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken, username, new GetUserHandler(observer));
        executeTask(getUserTask);
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class LoginHandler extends AuthHandler {
        public LoginHandler(AuthObserver observer) { super(observer, "login"); }
    }

    /**
     * Message handler (i.e., observer) for RegisterTask
     */
    private class RegisterHandler extends AuthHandler {
        public RegisterHandler(AuthObserver inObs) { super(inObs, "register"); }
    }

    /**
     * Message handler (i.e., observer) for LogoutTask
     */
    private class LogoutHandler extends NoDataReturnedHandler {
        public LogoutHandler(NoDataReturnedObserver inObs) { super(inObs, "logout"); }
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private class GetUserHandler extends BackgroundTaskHandler<GetUserObserver> {
        public GetUserHandler(GetUserObserver inObs) { super(inObs, "get user's profile"); }

        @Override
        protected void handleSuccessMessage(GetUserObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            observer.getUserSuccess(user);
        }
    }
}
