package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;

/**
 * A paged response for a {@link GetFollowersRequest}.
 */
public class GetFollowersResponse extends PagedResponse<User> {
    public GetFollowersResponse(List<User> followers, boolean hasMorePages) {
        super(followers, hasMorePages);
    }

    public GetFollowersResponse(String message) {
        super(message);
    }
}
