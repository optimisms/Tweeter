package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class RegisterPresenter extends AuthPresenter {
    public RegisterPresenter(AuthView inView) { super(inView); }

    @Override
    protected String validateInputs(String firstName, String lastName, String username, String password, Bitmap image) {
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
    protected void callServiceMethod(String firstName, String lastName, String username, String password, Bitmap image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

        new UserService().register(firstName, lastName, username, password, imageBytesBase64, this);
    }

    @Override
    protected String getValidateSuccessMessage() {
        return "Registering...";
    }

//    public void initiateRegister(String firstName, String lastName, String username, String password, Bitmap image) {
//        String message = validateRegistration(firstName, lastName, username, password, image);
//        if (message != null) { //Register invalid
//            ((AuthView) mView).clearInfoMessage();
//            ((AuthView) mView).displayErrorMessage(message);
//        } else { //Register valid
//            // Convert image to byte array.
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            byte[] imageBytes = bos.toByteArray();
//
//            // Intentionally, Use the java Base64 encoder so it is compatible with M4.
//            String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);
//
//            ((AuthView) mView).clearErrorMessage();
//            ((AuthView) mView).displayInfoMessage("Registering...");
//            new UserService().register(firstName, lastName, username, password, imageBytesBase64, this);
//        }
//    }
}
