package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service {
    public interface LoginObserver extends Observer {
        void loginSuccess(User user, AuthToken authToken);
    }
    public interface RegisterObserver extends Observer {
        void registerSuccess(User user, AuthToken authToken);
    }
    public interface LogoutObserver extends Observer {
        void logoutSuccess();
    }
    public interface GetUserObserver extends Observer {
        void getUserSuccess(User user);
    }

    public void login(String username, String password, LoginObserver observer) {
        // Send the login request.
        LoginTask loginTask = new LoginTask(username, password, new LoginHandler(observer));
        executeTask(loginTask);
    }
    public void register(String firstName, String lastName, String username, String password, String imageBytes, RegisterObserver observer) {
        // Send the register request.
        RegisterTask registerTask = new RegisterTask(firstName, lastName, username, password, imageBytes, new RegisterHandler(observer));
        executeTask(registerTask);
    }
    public void logout(AuthToken authToken, LogoutObserver observer) {
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
    private class LoginHandler extends BackgroundTaskHandler<LoginObserver> {
        public LoginHandler(LoginObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(LoginObserver observer, Bundle data) {
            User loggedInUser = (User) data.getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(LoginTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.loginSuccess(loggedInUser, authToken);
        }

        @Override
        protected void handleFailureMessage(LoginObserver observer, String message) {
            observer.taskFailed("Failed to login: " + message);
        }

        @Override
        protected void handleExceptionMessage(LoginObserver observer, String message) {
            observer.taskFailed("Failed to login because of exception: " + message);
        }
    }

    /**
     * Message handler (i.e., observer) for RegisterTask
     */
    private class RegisterHandler extends BackgroundTaskHandler<RegisterObserver>{
        public RegisterHandler(RegisterObserver inObs) { super(inObs); }

        @Override
        protected void handleSuccessMessage(RegisterObserver observer, Bundle data) {
            User registeredUser = (User) data.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.registerSuccess(registeredUser, authToken);
        }

        @Override
        protected void handleFailureMessage(RegisterObserver observer, String message) {
            observer.taskFailed("Failed to register: " + message);
        }

        @Override
        protected void handleExceptionMessage(RegisterObserver observer, String message) {
            observer.taskFailed("Failed to register because of exception: " + message);
        }
    }

    /**
     * Message handler (i.e., observer) for LogoutTask
     */
    private class LogoutHandler extends BackgroundTaskHandler<LogoutObserver> {
        public LogoutHandler(LogoutObserver inObs) { super(inObs); }

        @Override
        protected void handleSuccessMessage(LogoutObserver observer, Bundle data) {
            observer.logoutSuccess();
        }

        @Override
        protected void handleFailureMessage(LogoutObserver observer, String message) {
            observer.taskFailed("Failed to logout: " + message);
        }

        @Override
        protected void handleExceptionMessage(LogoutObserver observer, String message) {
            observer.taskFailed("Failed to logout because of exception: " + message);
        }
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private class GetUserHandler extends Handler {
        GetUserObserver mObserver;

        public GetUserHandler(GetUserObserver inObs) { mObserver = inObs; }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
            if (success) {
                User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
                mObserver.getUserSuccess(user);
            } else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
                mObserver.taskFailed("Failed to get user's profile: " + message);
            } else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
                mObserver.taskFailed("Failed to get user's profile because of exception: " + ex.getMessage());
            }
        }
    }
}
