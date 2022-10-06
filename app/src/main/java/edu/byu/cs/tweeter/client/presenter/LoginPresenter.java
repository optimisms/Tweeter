package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class LoginPresenter extends AuthPresenter {
    public LoginPresenter(AuthView inView) { super(inView); }

    @Override
    protected String validateInputs(String firstName, String lastName, String username, String password, Bitmap image) {
        if (username.charAt(0) != '@') { return "Username must begin with @."; }
        if (username.length() < 2) { return "Alias must contain 1 or more characters after the @."; }
        if (password.length() == 0) { return "Password cannot be empty."; }
        return null;
    }

    @Override
    protected void callServiceMethod(String firstName, String lastName, String username, String password, Bitmap image) {
        new UserService().login(username, password, this);
    }

    @Override
    protected String getValidateSuccessMessage() { return "Logging in..."; }
}
