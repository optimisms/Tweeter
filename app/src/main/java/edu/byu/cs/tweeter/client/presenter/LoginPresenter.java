package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter implements UserService.LoginObserver {
    public interface View {
        void displayInfoMessage(String message);
        void clearInfoMessage();
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void navigateToUser(User user);
    }

    private View mView;

    public LoginPresenter(View inView) { mView = inView; }

    public void initiateLogin(String username, String password) {
        String message = validateLogin(username, password);
        if (message != null) { //Login invalid
            mView.clearInfoMessage();
            mView.displayErrorMessage(message); }
        else { //Login valid
            mView.clearErrorMessage();
            mView.displayInfoMessage("Logging in...");
            new UserService().login(username, password, this);
        }
    }

    public String validateLogin(String username, String password) {
        if (username.charAt(0) != '@') { return "Username must begin with @."; }
        if (username.length() < 2) { return "Alias must contain 1 or more characters after the @."; }
        if (password.length() == 0) { return "Password cannot be empty."; }
        return null;
    }

    @Override
    public void loginSuccess(User user, AuthToken authToken) {
        mView.clearInfoMessage();
        mView.clearErrorMessage();
        mView.navigateToUser(user);
    }

    @Override
    public void loginFailed(String message) {
        mView.clearInfoMessage();
        mView.displayErrorMessage(message);
    }
}
