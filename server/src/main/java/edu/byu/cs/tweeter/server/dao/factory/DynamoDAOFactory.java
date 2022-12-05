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

    //TODO: don't forget to make sure that enhanced client hasn't been created before creating it
    //Check everywhere!!! Do not create multiple!!!
    //Maybe talk to TAs about how best to do this

    public DynamoDAOFactory() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(Region.US_WEST_2).build();
        enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }

    @Override
    public Database<AuthToken> getAuthTokenDAO() {
        return new AuthTokenDAO(enhancedClient);
    }

    @Override
    public Database<User> getUsersDAO() {
        return new UsersDAO(enhancedClient);
    }

    @Override
    public Database<Follow> getFollowDAO() {
        return new NewFollowDAO(enhancedClient);
    }

    @Override
    public Database<Status> getFeedDAO() {
        return new FeedDAO(enhancedClient);
    }

    @Override
    public Database<Status> getStoryDAO() {
        return new StoryDAO(enhancedClient);
    }
}
