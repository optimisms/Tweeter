package edu.byu.cs.tweeter.server.net.request;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class WriteBatchRequest extends Request {
    private List<String> followerAliases;
    private Status status;

    private WriteBatchRequest() {}

    public WriteBatchRequest(List<String> followerAliases, Status status) {
        this.followerAliases = followerAliases;
        this.status = status;
    }

    public List<String> getFollowerAliases() {
        return followerAliases;
    }

    public void setFollowerAliases(List<String> followerAliases) {
        this.followerAliases = followerAliases;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
