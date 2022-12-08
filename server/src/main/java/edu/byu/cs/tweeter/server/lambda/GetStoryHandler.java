package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class GetStoryHandler implements RequestHandler<PagedRequest<Status>, GetStoryResponse> {
    @Override
    public GetStoryResponse handleRequest(PagedRequest<Status> request, Context context) {
        return new StatusService(DynamoDAOFactory.getInstance()).getStory(request);
    }
}
