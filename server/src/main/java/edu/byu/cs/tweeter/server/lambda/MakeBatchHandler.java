package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class MakeBatchHandler implements RequestHandler<SQSEvent, PostStatusResponse> {
    @Override
    public PostStatusResponse handleRequest(SQSEvent event, Context context) {
        return new StatusService(DynamoDAOFactory.getInstance()).makeBatch(event);
    }
}
