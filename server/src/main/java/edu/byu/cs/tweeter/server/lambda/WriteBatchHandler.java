package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class WriteBatchHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            new StatusService(DynamoDAOFactory.getInstance()).writeBatch(msg);
        }
        return null;
    }
}
