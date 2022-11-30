package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowRequest extends Request {
    private User unfollower;
    private User unfollowee;

    private UnfollowRequest() {}

    public UnfollowRequest(User unfollower, User unfollowee) {
        this.unfollower = unfollower;
        this.unfollowee = unfollowee;
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
