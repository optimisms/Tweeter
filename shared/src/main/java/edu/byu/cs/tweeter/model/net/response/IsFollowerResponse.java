package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response {
    private boolean follower;

    public IsFollowerResponse(boolean follower) {
        super(true);
        this.follower = follower;
    }

    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public boolean isFollower() {
        return follower;
    }
}
