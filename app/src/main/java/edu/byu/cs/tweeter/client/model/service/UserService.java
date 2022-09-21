package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.complete.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.complete.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.complete.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {
    public interface LoginObserver {
        void loginSuccess(User user, AuthToken authToken);
        void loginFailed(String message);
    }

    public interface RegisterObserver {
        void registerSuccess(User user, AuthToken authToken);
        void registerFailed(String message);
    }

    public interface LogoutObserver {
        void logoutSuccess();
        void logoutFailed(String message);
    }

    public void login(String username, String password, LoginObserver observer) {
        // Send the login request.
        LoginTask loginTask = new LoginTask(username, password, new LoginHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask);
    }

    public void register(String firstName, String lastName, String username, String password, String imageBytes, RegisterObserver observer) {
        // Send the register request.
        RegisterTask registerTask = new RegisterTask(firstName, lastName, username, password, imageBytes, new RegisterHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(registerTask);
    }

    public void logout(AuthToken authToken, LogoutObserver observer) {
        // Send the logout request.
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(logoutTask);
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class LoginHandler extends Handler {
        private LoginObserver mObserver;

        public LoginHandler(LoginObserver inObs) { mObserver = inObs; }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LoginTask.SUCCESS_KEY);
            if (success) {
                User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

                Cache.getInstance().setCurrUser(loggedInUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                mObserver.loginSuccess(loggedInUser, authToken);
            } else if (msg.getData().containsKey(LoginTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LoginTask.MESSAGE_KEY);
                mObserver.loginFailed("Failed to login: " + message);
            } else if (msg.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LoginTask.EXCEPTION_KEY);
                mObserver.loginFailed("Failed to login because of exception" + ex.getMessage());
            }
        }
    }

    /**
     * Message handler (i.e., observer) for RegisterTask
     */
    private class RegisterHandler extends Handler {
        private RegisterObserver mObserver;

        public RegisterHandler(RegisterObserver inObs) { mObserver = inObs; }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(RegisterTask.SUCCESS_KEY);
            if (success) {
                User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

                Cache.getInstance().setCurrUser(registeredUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                mObserver.registerSuccess(registeredUser, authToken);
            } else if (msg.getData().containsKey(RegisterTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(RegisterTask.MESSAGE_KEY);
                mObserver.registerFailed("Failed to register: " + message);
            } else if (msg.getData().containsKey(RegisterTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(RegisterTask.EXCEPTION_KEY);
                mObserver.registerFailed("Failed to register because of exception: " + ex.getMessage());
            }
        }
    }

    /**
     * Message handler (i.e., observer) for LogoutTask
     */
    private class LogoutHandler extends Handler {
        LogoutObserver mObserver;

        public LogoutHandler(LogoutObserver inObs) { mObserver = inObs; }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LogoutTask.SUCCESS_KEY);
            if (success) {
                mObserver.logoutSuccess();
            } else if (msg.getData().containsKey(LogoutTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LogoutTask.MESSAGE_KEY);
                mObserver.logoutFailed("Failed to logout: " + message);
            } else if (msg.getData().containsKey(LogoutTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LogoutTask.EXCEPTION_KEY);
                mObserver.logoutFailed("Failed to logout because of exception: " + ex.getMessage());
            }
        }
    }
}
