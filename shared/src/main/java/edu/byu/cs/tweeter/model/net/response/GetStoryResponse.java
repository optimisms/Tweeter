package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryResponse extends PagedResponse<Status> {
    public GetStoryResponse(List<Status> statuses, boolean hasMorePages) {
        super(statuses, hasMorePages);
    }

    public GetStoryResponse(String message) {
        super(message);
    }
}
