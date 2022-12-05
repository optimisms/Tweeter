package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.Database;
import edu.byu.cs.tweeter.server.dao.dynamo.DTO.UserBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class UsersDAO implements Database<User> {
    private static final String TABLE_NAME = "tweeter_users";
    private static final String USER_ALIAS_ATTR = "user_alias";

    private final DynamoDbEnhancedClient enhancedClient;

    public UsersDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    @Override
    public User get(String user_alias) throws DataAccessException {
        DynamoDbTable<UserBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserBean.class));
        Key key = Key.builder().partitionValue(user_alias).build();

        UserBean user = table.getItem(key);

        if (user == null) {
            throw new DataAccessException("Item not found at PartitionKey (" + USER_ALIAS_ATTR + ":" + user_alias + ")");
        } else {
            return new User(user.getFirst_name(), user.getLast_name(), user.getUser_alias(), user.getImage_url());
        }
    }

    //TODO: implement
    @Override
    public User get(String partition, String sort) throws DataAccessException {
        return null;
    }

    @Override
    public void add(User toAdd) {
        DynamoDbTable<UserBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserBean.class));

        UserBean newUser = new UserBean();
        newUser.setUser_alias(toAdd.getAlias());
        newUser.setFirst_name(toAdd.getFirstName());
        newUser.setLast_name(toAdd.getLastName());
        newUser.setImage_url(toAdd.getImageUrl());

        //TODO: add check that item does not already exist in table and throw exception if true
        table.putItem(newUser);
    }

    @Override
    public void update(User toUpdate) {

    }

    @Override
    public void delete(User toDelete) {

    }
}
