package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowRequest extends Request {
    private AuthToken token;
    private User unfollower;
    private User unfollowee;

    private UnfollowRequest() {}

    public UnfollowRequest(AuthToken token, User unfollower, User unfollowee) {
        this.token = token;
        this.unfollower = unfollower;
        this.unfollowee = unfollowee;
    }

    public AuthToken getToken() {
        return token;
    }

    public void setToken(AuthToken token) {
        this.token = token;
    }

    public User getUnfollower() {
        return unfollower;
    }

    public void setUnfollower(User unfollower) {
        this.unfollower = unfollower;
    }

    public User getUnfollowee() {
        return unfollowee;
    }

    public void setUnfollowee(User unfollowee) {
        this.unfollowee = unfollowee;
    }
}
