package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.Database;
import edu.byu.cs.tweeter.server.dao.dynamo.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FeedDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.NewFollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.StoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.UsersDAO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDAOFactory implements DAOFactory {
    private final DynamoDbEnhancedClient enhancedClient;
    private final Database<AuthToken> authTokenDAO;
    private final Database<User> userDAO;
    private final Database<Follow> followDAO;
    private final Database<Status> feedDAO;
    private final Database<Status> storyDAO;

    private static DynamoDAOFactory instance;


    //TODO: don't forget to make sure that enhanced client hasn't been created before creating it
    //Check everywhere!!! Do not create multiple!!!
    //Maybe talk to TAs about how best to do this

    private DynamoDAOFactory() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.US_WEST_2).build();
        enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();

        authTokenDAO = new AuthTokenDAO(enhancedClient);
        feedDAO = new FeedDAO(enhancedClient);
        storyDAO = new StoryDAO(enhancedClient);
        followDAO = new NewFollowDAO(enhancedClient);
        userDAO = new UsersDAO(enhancedClient);
    }

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
    public Database<Follow> getFollowDAO() {
        return followDAO;
    }

    @Override
    public Database<Status> getFeedDAO() {
        return feedDAO;
    }

    @Override
    public Database<Status> getStoryDAO() {
        return storyDAO;
    }

//    public DynamoDbEnhancedClient getClient() {
//        if (enhancedClient == null)
//        {
//            DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.US_WEST_2).build();
//            enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
//        }
//        return enhancedClient;
//    }
}
