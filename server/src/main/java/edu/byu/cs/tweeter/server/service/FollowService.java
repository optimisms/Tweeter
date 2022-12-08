package edu.byu.cs.tweeter.server.service;

import java.text.ParseException;
import java.util.List;

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
import edu.byu.cs.tweeter.server.dao.PagedDatabase;
import edu.byu.cs.tweeter.server.dao.dynamo.FollowDAO;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        try {
            if (request.getFollower() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a follower");
            } else if (request.getFollowee() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a followee");
            } else if (request.getToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            Follow relationship = getFollowDAO().get(request.getFollower().getAlias(), request.getFollowee().getAlias());

            if (relationship != null) { return new IsFollowerResponse(true); }
            else { return new IsFollowerResponse(false); }
        } catch (DataAccessException | RuntimeException | ParseException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("Item not found at PartitionKey (")) {
                return new IsFollowerResponse(false);
            } else if (e.getMessage().startsWith("[BadRequest]")) {
                return new IsFollowerResponse(e.getMessage());
            } else {
                return new IsFollowerResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public FollowResponse follow(FollowRequest request) {
        try {
            if (request.getFollower() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a follower");
            } else if (request.getFollowee() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a followee");
            } else if (request.getToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            Follow toAdd = new Follow(request.getFollower(), request.getFollowee());

            getFollowDAO().add(toAdd);

            User follower = getUserDAO().get(request.getFollower().getAlias(), null);
            follower.setFollowingCount(follower.getFollowingCount() + 1);
            getUserDAO().update(follower);

            User followee = getUserDAO().get(request.getFollowee().getAlias(), null);
            followee.setFollowerCount(followee.getFollowerCount() + 1);
            getUserDAO().update(followee);

            return new FollowResponse();
        } catch (DataAccessException | RuntimeException | ParseException e) {
            e.printStackTrace();

            if (e.getMessage().startsWith("[BadRequest]")) {
                return new FollowResponse(e.getMessage());
            } else {
                return new FollowResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        try {
            if (request.getUnfollower() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a unfollower");
            } else if (request.getUnfollowee() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a unfollowee");
            } else if (request.getToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            Follow toDelete = new Follow(request.getUnfollower(), request.getUnfollowee());

            getFollowDAO().delete(toDelete);

            User unfollower = getUserDAO().get(request.getUnfollower().getAlias(), null);
            unfollower.setFollowingCount(unfollower.getFollowingCount() - 1);
            getUserDAO().update(unfollower);

            User unfollowee = getUserDAO().get(request.getUnfollowee().getAlias(), null);
            unfollowee.setFollowerCount(unfollowee.getFollowerCount() - 1);
            getUserDAO().update(unfollowee);

            return new UnfollowResponse();
        } catch (DataAccessException | RuntimeException | ParseException e) {
            e.printStackTrace();

            if (e.getMessage().startsWith("[BadRequest]")) {
                return new UnfollowResponse(e.getMessage());
            } else {
                return new UnfollowResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public GetFollowingResponse getFollowees(PagedRequest<User> request) {
        try {
            if(request.getTargetUserAlias() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
            } else if(request.getLimit() <= 0) {
                throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
            } else if (request.getAuthToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getAuthToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            String lastFolloweeAlias = request.getLastItem() == null ? null : request.getLastItem().getAlias();
            List<User> following = getFollowDAO().getPages(request.getTargetUserAlias(), request.getLimit(), lastFolloweeAlias, "getFollowing");

            return new GetFollowingResponse(following, following.size() == request.getLimit());
        } catch (DataAccessException | RuntimeException | ParseException e) {
            e.printStackTrace();

            if (e.getMessage().startsWith("[BadRequest]")) {
                return new GetFollowingResponse(e.getMessage());
            } else {
                return new GetFollowingResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public GetFollowersResponse getFollowers(PagedRequest<User> request) {
        try {
            if(request.getTargetUserAlias() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a followee alias");
            } else if(request.getLimit() <= 0) {
                throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
            } else if (request.getAuthToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getAuthToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            String lastFollowerAlias = request.getLastItem() == null ? null : request.getLastItem().getAlias();
            List<User> followers = getFollowDAO().getPages(request.getTargetUserAlias(), request.getLimit(), lastFollowerAlias, "getFollowers");

            return new GetFollowersResponse(followers, followers.size() == request.getLimit());
        } catch (DataAccessException | RuntimeException | ParseException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("[BadRequest]")) {
                return new GetFollowersResponse(e.getMessage());
            } else {
                return new GetFollowersResponse("[Internal Server Error] " + e.getMessage());
            }
        }

    }

    public CountResponse getFollowersCount(CountRequest request) {
        try {
            if (request.getTargetUser() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a target user");
            } else if (request.getToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            User user = getUserDAO().get(request.getTargetUser().getAlias(), null);

            return new CountResponse(user.getFollowerCount());
        } catch (DataAccessException | RuntimeException | ParseException e) {
            e.printStackTrace();

            if (e.getMessage().startsWith("[BadRequest]")) {
                return new CountResponse(e.getMessage());
            } else {
                return new CountResponse("[Internal Server Error] " + e.getMessage());
            }
        }
    }

    public CountResponse getFollowingCount(CountRequest request) {
        try {
            if (request.getTargetUser() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have a target user");
            } else if (request.getToken() == null) {
                throw new RuntimeException("[BadRequest] Request needs to have an authToken");
            } else if (!AuthService.tokenIsValid(request.getToken())) {
                throw new RuntimeException("[BadRequest] Request needs to have a valid authToken");
            }

            User user = getUserDAO().get(request.getTargetUser().getAlias(), null);

            return new CountResponse(user.getFollowingCount());
        } catch (DataAccessException | ParseException e) {
            e.printStackTrace();

            if (e.getMessage().startsWith("[BadRequest]")) {
                return new CountResponse(e.getMessage());
            } else {
                return new CountResponse("[Internal Server Error] " + e.getMessage());
            }

        }
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    PagedDatabase<Follow, User> getFollowDAO() { return DynamoDAOFactory.getInstance().getFollowDAO(); }
    Database<User> getUserDAO() { return DynamoDAOFactory.getInstance().getUsersDAO(); }
}
