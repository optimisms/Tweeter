package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.Database;
import edu.byu.cs.tweeter.server.dao.FollowDatabase;
import edu.byu.cs.tweeter.server.dao.PagedDatabase;
import edu.byu.cs.tweeter.server.dao.dynamo.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FeedDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.StoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.UsersDAO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDAOFactory implements DAOFactory {
    private final DynamoDbEnhancedClient enhancedClient;
    private final Database<AuthToken> authTokenDAO;
    private final Database<User> userDAO;
    private final FollowDatabase followDAO;
    private final PagedDatabase<Status, Status> feedDAO;
    private final PagedDatabase<Status, Status> storyDAO;

    private static DynamoDAOFactory instance;

    private DynamoDAOFactory() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.US_WEST_2).build();
        enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();

        authTokenDAO = new AuthTokenDAO(enhancedClient);
        feedDAO = new FeedDAO(enhancedClient);
        storyDAO = new StoryDAO(enhancedClient);
        followDAO = new FollowDAO(enhancedClient);
        userDAO = new UsersDAO(enhancedClient);
    }

    //TODO: ask Dr. Rodham if there's a way to put this in the parent class and use the static AND abstract functionality together
    // Right now I can't put it in the parent (when abstract class, not interface) bc it has the new() declaration which can't be
    // used for an abstract class
    public static DynamoDAOFactory getInstance() {
        if (instance == null) {
            synchronized(DynamoDAOFactory.class) {
                if (instance == null) {
                    instance = new DynamoDAOFactory();
                }
            }
        }

        return instance;
    }

    @Override
    public Database<AuthToken> getAuthTokenDAO() {
        return authTokenDAO;
    }

    @Override
    public Database<User> getUsersDAO() {
         return userDAO;
    }

    @Override
    public FollowDatabase getFollowDAO() {
        return followDAO;
    }

    @Override
    public PagedDatabase<Status, Status> getFeedDAO() {
        return feedDAO;
    }

    @Override
    public PagedDatabase<Status, Status> getStoryDAO() {
        return storyDAO;
    }
}
