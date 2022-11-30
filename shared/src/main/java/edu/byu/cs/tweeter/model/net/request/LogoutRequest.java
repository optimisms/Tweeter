package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class LogoutRequest extends Request {
    private AuthToken token;

    private LogoutRequest() {}

    public LogoutRequest(AuthToken authToken) {
        this.token = authToken;
    }

    public AuthToken getToken() {
        return token;
    }

    public void setToken(AuthToken token) {
        this.token = token;
    }
}
