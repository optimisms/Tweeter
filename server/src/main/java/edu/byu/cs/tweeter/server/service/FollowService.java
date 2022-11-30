package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public GetFollowingResponse getFollowees(PagedRequest<User> request) {
        if(request.getTargetUserAlias() == null) {
//            return new GetFollowingResponse("[BadRequest] Request needs to have a follower alias");
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
//            return new GetFollowingResponse("[BadRequest] Request needs to have a positive limit");
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getFollowDAO().getFollowees(request);
    }

    public GetFollowersResponse getFollowers(PagedRequest<User> request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getFollowDAO().getFollowers(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if (request.getFollower() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower");
        } else if (request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee");
        }

        //TODO: Use DAOs instead
        return new IsFollowerResponse(true);
    }

    public FollowResponse follow(FollowRequest request) {
        if (request.getFollower() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower");
        } else if (request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee");
        }

        //TODO use DAOs
        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request.getUnfollower() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a unfollower");
        } else if (request.getUnfollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a unfollowee");
        }

        //TODO use DAOs
        return new UnfollowResponse();
    }

    public CountResponse getFollowersCount(CountRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        }

        return new CountResponse(getFollowDAO().getFollowerCount(request.getTargetUser()));
    }

    public CountResponse getFollowingCount(CountRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        }

        return new CountResponse(getFollowDAO().getFolloweeCount(request.getTargetUser()));
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowDAO() {
        return new FollowDAO();
    }
}
