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
        //new UserService().login(username, password, this);  //Pretty sure this is wrong but idr how

    }

    @Override
    public void loginSuccess(User user, AuthToken authToken) {

    }

    @Override
    public void loginFailed(String message) {

    }
}
