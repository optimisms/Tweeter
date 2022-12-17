package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

//TODO: FIX INHERITANCE STRUCTURE HERE!!! PagedDatabase should be inheriting from Database, not Dynamo!!
// Also fix for Follow, Feed, and UserDatabase if necessary; not sure how this got so mixed up??
public abstract class PagedDatabase<T, U> extends DynamoDAO<T> {
    protected PagedDatabase(DynamoDbEnhancedClient enhancedClient) {
        super(enhancedClient);
    }

    public abstract List<U> getPages(String partition_key, int pageSize, String sort_start_key, String taskType) throws DataAccessException;

    protected static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }
}
