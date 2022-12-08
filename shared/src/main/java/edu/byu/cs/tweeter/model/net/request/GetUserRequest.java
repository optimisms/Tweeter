package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetUserRequest extends Request {
    private AuthToken token;
    private String alias;

    private GetUserRequest(){}

    public GetUserRequest(AuthToken token, String alias) {
        this.token = token;
        this.alias = alias;
    }

    public AuthToken getToken() {
        return token;
    }

    public void setToken(AuthToken token) {
        this.token = token;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
