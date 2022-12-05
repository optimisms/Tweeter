package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticateTask<RegisterRequest, AuthResponse> {
    private static final String LOG_TAG = "RegisterTask";

    /**
     * The user's first name.
     */
    private final String firstName;

    /**
     * The user's last name.
     */
    private final String lastName;

    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private final String image;

    public RegisterTask(String firstName, String lastName, String username, String password,
                        String image, Handler messageHandler) {
        super(messageHandler, username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
    }

    @Override
    protected RegisterRequest getAuthRequest() {
        return new RegisterRequest(username, password, firstName, lastName, image);
    }

    @Override
    protected AuthResponse getAuthResponse(RegisterRequest request) throws IOException, TweeterRemoteException {
        return getServerFacade().register(request, UserService.REGISTER_URL_PATH);
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }
}
