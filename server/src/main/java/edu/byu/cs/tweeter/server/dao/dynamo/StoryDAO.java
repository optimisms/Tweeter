package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.PagedDatabase;
import edu.byu.cs.tweeter.server.dao.dynamo.DTO.StoryBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class StoryDAO extends PagedDatabase<Status, Status> {
    private static final String TABLE_NAME = "tweeter_story";
    private static final String SENDER_ALIAS_ATTR = "sender_alias";
    private static final String TIMESTAMP_ATTR = "timestamp";

    public StoryDAO(DynamoDbEnhancedClient enhancedClient) {
        super(enhancedClient);
    }

    //TODO: implement
    @Override
    public Status get(String sender_alias, String timestamp) {
        return null;
        //Could implement if we had a way to view a single status
    }

    @Override
    public void add(Status toAdd) {
        DynamoDbTable<StoryBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(StoryBean.class));

        StoryBean newStatus = new StoryBean();
        newStatus.setPost(toAdd.getPost());
        newStatus.setSender_alias(toAdd.getUser().getAlias());
        newStatus.setSender_firstName(toAdd.getUser().getFirstName());
        newStatus.setSender_lastName(toAdd.getUser().getLastName());
        newStatus.setSender_imageUrl(toAdd.getUser().getImageUrl());
        newStatus.setTimestamp(toAdd.getDate());
        newStatus.setUrls(toAdd.getUrls());
        newStatus.setMentions(toAdd.getMentions());

        table.putItem(newStatus);
    }

    //TODO: implement
    @Override
    public void update(Status toUpdate) {
// Could implement if we had a way to edit statuses
    }

    @Override
    public void delete(Status toDelete) {
// Could implement if we had a way to delete statuses
    }

    @Override
    public List<Status> getPages(String partition_key, int pageSize, String sort_start_key, String taskType) throws DataAccessException {
        return null;
    }
}
