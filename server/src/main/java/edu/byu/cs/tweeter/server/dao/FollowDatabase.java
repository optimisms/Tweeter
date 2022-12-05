package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;

public interface FollowDatabase extends Database<Follow> {
    List<User> getFollowing(String follower_handle, int pageSize, String last_followee_handle) throws DataAccessException;
    List<User> getFollowers(String followee_handle, int pageSize, String last_follower_handle) throws DataAccessException;
}
