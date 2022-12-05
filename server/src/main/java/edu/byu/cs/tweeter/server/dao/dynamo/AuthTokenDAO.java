package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.Database;
import edu.byu.cs.tweeter.server.dao.dynamo.DTO.AuthTokenBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class AuthTokenDAO implements Database<AuthToken> {
    private static final String TABLE_NAME = "tweeter_auth_tokens";
    private static final String AUTH_TOKEN_ATTR = "auth_token";

    private final DynamoDbEnhancedClient enhancedClient;

    public AuthTokenDAO(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    @Override
    public AuthToken get(String auth_token, String sort) throws DataAccessException {
        //IGNORE sort

        DynamoDbTable<AuthTokenBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(AuthTokenBean.class));
        Key key = Key.builder().partitionValue(auth_token).build();

        AuthTokenBean token = table.getItem(key);

        if (token == null) {
            throw new DataAccessException("Item not found at PartitionKey (" + AUTH_TOKEN_ATTR + ":" + auth_token + ")");
        } else {
            return new AuthToken(token.getToken(), token.getDatetime());
        }
    }

    @Override
    public void add(AuthToken toAdd) throws DataAccessException {
        DynamoDbTable<AuthTokenBean> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(AuthTokenBean.class));

        AuthTokenBean newToken = new AuthTokenBean();
        newToken.setToken(toAdd.getToken());
        try {
            get(newToken.getToken(), null);
        } catch (DataAccessException e) {
            //If item does not exist, add it
            newToken.setDatetime(toAdd.getDatetime());

            table.putItem(newToken);
            return;
        }
        //If item does exist, throw exception
        throw new DataAccessException("This authToken already exists.");
    }

    @Override
    public void update(AuthToken toUpdate) {
        //TODO: implement for renewing tokens
    }

    @Override
    public void delete(AuthToken toDelete) {
        //TODO: implement for destroying when expired or logged out
    }
}
