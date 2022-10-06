package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.handlers.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service {
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

    //TODO: Combine AuthHandlers
    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class LoginHandler extends BackgroundTaskHandler<AuthObserver> {
        public LoginHandler(AuthObserver observer) {
            super(observer, "login");
        }

        @Override
        protected void handleSuccessMessage(AuthObserver observer, Bundle data) {
            User loggedInUser = (User) data.getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(LoginTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.authSuccess(loggedInUser, authToken);
        }
    }

    /**
     * Message handler (i.e., observer) for RegisterTask
     */
    private class RegisterHandler extends BackgroundTaskHandler<AuthObserver>{
        public RegisterHandler(AuthObserver inObs) { super(inObs, "register"); }

        @Override
        protected void handleSuccessMessage(AuthObserver observer, Bundle data) {
            User registeredUser = (User) data.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.authSuccess(registeredUser, authToken);
        }
    }

    /**
     * Message handler (i.e., observer) for LogoutTask
     */
    private class LogoutHandler extends BackgroundTaskHandler<NoDataReturnedObserver> {
        public LogoutHandler(NoDataReturnedObserver inObs) { super(inObs, "logout"); }

        @Override
        protected void handleSuccessMessage(NoDataReturnedObserver observer, Bundle data) {
            observer.taskSuccess();
        }
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
