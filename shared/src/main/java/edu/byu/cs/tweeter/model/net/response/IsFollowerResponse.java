package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response {
    //TODO: find out where the name of this variable is coming from and
    // why it didn't work until I changed it from isFollower to follower
    // (see Lambda Response JSON)
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
