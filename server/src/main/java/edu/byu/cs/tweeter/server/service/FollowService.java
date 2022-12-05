package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.Follow;
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
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.Database;
import edu.byu.cs.tweeter.server.dao.dynamo.FollowDAO;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    //TODO: Check right error handling for all methods

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if (request.getFollower() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower");
        } else if (request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee");
        }

        try {
            Follow relationship = getNewFollowDAO().get(request.getFollower().getAlias(), request.getFollowee().getAlias());

            if (relationship != null) { return new IsFollowerResponse(true); }
            else { return new IsFollowerResponse(false); }
        } catch (DataAccessException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("Item not found at PartitionKey (")) {
                return new IsFollowerResponse(false);
            } else {
                throw new RuntimeException("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public FollowResponse follow(FollowRequest request) {
        if (request.getFollower() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower");
        } else if (request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee");
        }

        try {
            Follow toAdd = new Follow(request.getFollower(), request.getFollowee());

            getNewFollowDAO().add(toAdd);

            return new FollowResponse();
        } catch (DataAccessException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("User " + request.getFollower().getAlias())) {
                return new FollowResponse(e.getMessage());
            } else {
                throw new RuntimeException("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request.getUnfollower() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a unfollower");
        } else if (request.getUnfollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a unfollowee");
        }

        try {
            Follow toDelete = new Follow(request.getUnfollower(), request.getUnfollowee());

            getNewFollowDAO().delete(toDelete);

            return new UnfollowResponse();
        } catch (DataAccessException e) {
            if (e.getMessage().startsWith("User " + request.getUnfollower().getAlias())) {
                return new UnfollowResponse(e.getMessage());
            } else {
                throw new RuntimeException("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    //TODO: Migrate all below to DAOs

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
    Database<Follow> getNewFollowDAO() { return DynamoDAOFactory.getInstance().getFollowDAO(); }

    FollowDAO getFollowDAO() { return new FollowDAO(); }
}
