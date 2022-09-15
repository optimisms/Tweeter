package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter implements UserService.LoginObserver {
    public interface View {
        public void displayInfoMessage(String message);
        public void clearInfoMessage();
        public void displayErrorMessage(String message);
        public void clearErrorMessage();
        public void navigateToUser(User user);
    }

    private View mView;

    public LoginPresenter(View inView) { mView = inView; }

    public void initiateLogin(String username, String password) {
        new UserService().login(username, password, this);  //Pretty sure this is wrong but idr how
        String message = validateLogin(username, password);
        if (message != null) { mView.displayErrorMessage(message); }
        else {
            mView.clearErrorMessage();
            mView.displayInfoMessage("Logging in...");
        }
    }

    public String validateLogin(String username, String password) {
        if (username.charAt(0) != '@') {return "Username must begin with @.";}
        if (username.length() < 2) {return "Alias must contain 1 or more characters after the @.";}
        if (password.length() == 0) {return "Password cannot be empty.";}
        else return null;
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
