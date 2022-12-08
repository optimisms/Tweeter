package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.PagedDatabase;
import edu.byu.cs.tweeter.server.dao.dynamo.DTO.StoryBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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
    public List<Status> getPages(String target_user_alias, int pageSize, String last_status_timestamp, String taskType) throws DataAccessException {
        //IGNORE taskType

        DynamoDbTable<StoryBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(StoryBean.class));
        Key key = Key.builder()
                .partitionValue(target_user_alias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true);

        if (isNonEmptyString(last_status_timestamp)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(SENDER_ALIAS_ATTR, AttributeValue.builder().s(target_user_alias).build());
            startKey.put(TIMESTAMP_ATTR, AttributeValue.builder().s(last_status_timestamp).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        List<StoryBean> beans = table.query(request).items().stream().limit(pageSize).collect(Collectors.toList());

        List<Status> toReturn = new ArrayList<>();
        for (StoryBean curr : beans) {
            User newUser = new User(curr.getSender_firstName(), curr.getSender_lastName(), curr.getSender_alias(), curr.getSender_imageUrl());
            Status newStatus = new Status(curr.getPost(), newUser, curr.getTimestamp(), curr.getUrls(), curr.getMentions());
            toReturn.add(newStatus);
        }
        return toReturn;
    }
}
