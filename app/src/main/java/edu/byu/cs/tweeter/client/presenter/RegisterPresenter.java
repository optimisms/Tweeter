package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends Presenter implements UserService.RegisterObserver {
    public interface RegisterView extends View {
        void displayInfoMessage(String message);
        void clearInfoMessage();
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void navigateToUser(User user);
    }

    private RegisterView mView;

    public RegisterPresenter(RegisterView inView) { super(inView); mView = inView; }

    public void initiateRegister(String firstName, String lastName, String username, String password, Bitmap image) {
        String message = validateRegistration(firstName, lastName, username, password, image);
        if (message != null) { //Register invalid
            mView.clearInfoMessage();
            mView.displayErrorMessage(message);
        } else { //Register valid
            // Convert image to byte array.
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();

            // Intentionally, Use the java Base64 encoder so it is compatible with M4.
            String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

            mView.clearErrorMessage();
            mView.displayInfoMessage("Registering...");
            new UserService().register(firstName, lastName, username, password, imageBytesBase64, this);
        }
    }

    public String validateRegistration(String firstName, String lastName, String username, String password, Bitmap image) {
        if (firstName.length() == 0) { return "First Name cannot be empty."; }
        if (lastName.length() == 0) { return "Last Name cannot be empty."; }
        if (username.length() == 0) { return "Alias cannot be empty."; }
        if (username.charAt(0) != '@') { return "Alias must begin with @."; }
        if (username.length() < 2) { return "Alias must contain 1 or more characters after the @."; }
        if (password.length() == 0) { return "Password cannot be empty."; }
        if (image == null) { return "Profile image must be uploaded."; }
        return null;
    }

    @Override
    public void registerSuccess(User user, AuthToken authToken) {
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
