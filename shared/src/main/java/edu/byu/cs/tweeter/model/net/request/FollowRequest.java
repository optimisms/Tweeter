package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest extends Request {
    private User follower;
    private User followee;

    private FollowRequest() {}

    public FollowRequest(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }
}
