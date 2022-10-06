package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter implements UserService.LoginObserver {
    public interface LoginView extends View {
        void displayInfoMessage(String message);
        void clearInfoMessage();
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void navigateToUser(User user);
    }

    private LoginView mView;

    public LoginPresenter(LoginView inView) { super(inView); mView = inView; }

    public void initiateLogin(String username, String password) {
        String message = validateLogin(username, password);
        if (message != null) { //Login invalid
            mView.clearInfoMessage();
            mView.displayErrorMessage(message);
        } else { //Login valid
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
        mView.displayInfoMessage("Hello " + user.getFirstName());
        mView.clearErrorMessage();
        mView.navigateToUser(user);
    }

    @Override
    public void taskFailed(String message) {
        mView.clearInfoMessage();
        mView.displayErrorMessage(message);
    }
}
