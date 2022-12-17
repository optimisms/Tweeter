package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.Database;
import edu.byu.cs.tweeter.server.dao.FeedDatabase;
import edu.byu.cs.tweeter.server.dao.FollowDatabase;
import edu.byu.cs.tweeter.server.dao.PagedDatabase;
import edu.byu.cs.tweeter.server.dao.UserDatabase;

public interface DAOFactory {
    Database<AuthToken> getAuthTokenDAO();
    UserDatabase getUsersDAO();
    FollowDatabase getFollowDAO();
    FeedDatabase getFeedDAO();
    PagedDatabase<Status, Status> getStoryDAO();
}
