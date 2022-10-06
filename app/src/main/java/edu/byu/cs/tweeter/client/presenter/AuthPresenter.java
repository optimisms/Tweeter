package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;

import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthPresenter extends Presenter implements Service.AuthObserver {
    public interface AuthView extends View {
        void displayInfoMessage(String message);
        void clearInfoMessage();
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void navigateToUser(User user);
    }

    public AuthPresenter(AuthView inView) {
        super(inView);
    }

    public void initiateAuthTask(String username, String password) {
        initiateAuthTask(null, null, username, password, null);
    }
    public void initiateAuthTask(String firstName, String lastName, String username, String password, Bitmap image) {
        String message = validateInputs(firstName, lastName, username, password, image);
        if (message != null) { //Inputs invalid
            ((AuthView) mView).clearInfoMessage();
            ((AuthView) mView).displayErrorMessage(message);
        } else { //Inputs valid
            ((AuthView) mView).clearErrorMessage();
            ((AuthView) mView).displayInfoMessage(getValidateSuccessMessage());
            callServiceMethod(firstName, lastName, username, password, image);
            //new UserService().login(username, password, this);

        }
    }

    protected abstract String validateInputs(String firstName, String lastName, String username, String password, Bitmap image);
    protected abstract void callServiceMethod(String firstName, String lastName, String username, String password, Bitmap image);
    protected abstract String getValidateSuccessMessage();

    @Override
    public void authSuccess(User user, AuthToken authToken) {
        ((AuthView) mView).displayInfoMessage("Hello " + user.getFirstName());
        ((AuthView) mView).clearErrorMessage();
        ((AuthView) mView).navigateToUser(user);
    }

    @Override
    public void taskFailed(String message) {
        ((AuthView) mView).clearInfoMessage();
        ((AuthView) mView).displayErrorMessage(message);
    }
}
