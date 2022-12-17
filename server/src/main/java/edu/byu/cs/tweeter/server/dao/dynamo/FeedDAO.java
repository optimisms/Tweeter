package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.FeedDatabase;
import edu.byu.cs.tweeter.server.dao.dynamo.DTO.FeedBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class FeedDAO extends FeedDatabase {
    private static final String TABLE_NAME = "tweeter_feed";
    private static final String RECEIVER_ALIAS_ATTR = "receiver_alias";
    private static final String TIMESTAMP_ATTR = "timestamp";

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
        DynamoDbTable<FeedBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FeedBean.class));

        FeedBean newStatus = new FeedBean(toAdd);

        table.putItem(newStatus);
    }

    @Override
    public void update(Status toUpdate) {

    }

    @Override
    public void delete(Status toDelete) {

    }

    @Override
    public List<Status> getPages(String target_user_alias, int pageSize, String last_status_timestamp, String taskType) throws DataAccessException {
        //IGNORE taskType

        DynamoDbTable<FeedBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FeedBean.class));
        Key key = Key.builder()
                .partitionValue(target_user_alias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(false);

        if (isNonEmptyString(last_status_timestamp)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(RECEIVER_ALIAS_ATTR, AttributeValue.builder().s(target_user_alias).build());
            startKey.put(TIMESTAMP_ATTR, AttributeValue.builder().s(last_status_timestamp).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        List<FeedBean> beans = table.query(request).items().stream().limit(pageSize).collect(Collectors.toList());

        List<Status> toReturn = new ArrayList<>();
        for (FeedBean curr : beans) {
            User newUser = new User(curr.getSender_firstName(), curr.getSender_lastName(), curr.getSender_alias(), curr.getSender_imageUrl());
            Status newStatus = new Status(curr.getPost(), newUser, curr.getTimestamp(), curr.getUrls(), curr.getMentions());
            toReturn.add(newStatus);
        }
        return toReturn;
    }

    @Override
    public void batchWrite(List<String> followers, Status status) {
        if(followers.size() > 25)
            throw new RuntimeException("Too many followers to write");

        DynamoDbTable<FeedBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(FeedBean.class));
        WriteBatch.Builder<FeedBean> writeBuilder = WriteBatch.builder(FeedBean.class).mappedTableResource(table);

        List<FeedBean> beans = makeFeedDTOs(followers, status);

        for (FeedBean item : beans) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
//            if (result.unprocessedPutItemsForTable(table).size() > 0) {
//                batchWrite(result.unprocessedPutItemsForTable(table));
//            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private List<FeedBean> makeFeedDTOs(List<String> followers, Status status) {
        List<FeedBean> beans = new ArrayList<>();
        for (String alias : followers) {
            FeedBean bean = new FeedBean(alias, status);
            beans.add(bean);
        }
        return beans;
    }
}