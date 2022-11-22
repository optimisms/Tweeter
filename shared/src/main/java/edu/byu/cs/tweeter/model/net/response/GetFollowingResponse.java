package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;

/**
 * A paged response for a {@link GetFollowingRequest}.
 */
public class GetFollowingResponse extends PagedResponse<User> {
    public GetFollowingResponse(List<User> followers, boolean hasMorePages) {
        super(followers, hasMorePages);
    }

    public GetFollowingResponse(String message) {
        super(message);
    }
}
