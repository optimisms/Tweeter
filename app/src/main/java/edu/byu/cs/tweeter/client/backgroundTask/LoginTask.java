package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask<LoginRequest, AuthResponse> {
    private static final String LOG_TAG = "LoginTask";

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected LoginRequest getAuthRequest() {
        return new LoginRequest(username, password);
    }

    @Override
    protected AuthResponse getAuthResponse(LoginRequest request) throws IOException, TweeterRemoteException {
        return getServerFacade().login(request, UserService.LOGIN_URL_PATH);
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {
        User loggedInUser = getFakeData().getFirstUser();
        AuthToken authToken = getFakeData().getAuthToken();
        return new Pair<>(loggedInUser, authToken);
    }
}
