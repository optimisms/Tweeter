package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.PagedDatabase;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public class FeedDAO extends PagedDatabase<Status, Status> {
    public FeedDAO(DynamoDbEnhancedClient enhancedClient) {
        super(enhancedClient);
    }

    //TODO: implement
    @Override
    public Status get(String partition, String sort) throws DataAccessException {
        return null;
    }

    @Override
    public void add(Status toAdd) {

    }

    @Override
    public void update(Status toUpdate) {

    }

    @Override
    public void delete(Status toDelete) {

    }

    @Override
    public List<Status> getPages(String partition_key, int pageSize, String sort_start_key, String taskType) throws DataAccessException {
        return null;
    }
}
