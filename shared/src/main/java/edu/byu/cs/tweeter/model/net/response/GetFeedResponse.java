package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedResponse extends PagedResponse<Status> {
    public GetFeedResponse(List<Status> statuses, boolean hasMorePages) {
        super(statuses, hasMorePages);
    }

    public GetFeedResponse(String message) {
        super(message);
    }
}