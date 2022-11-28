package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.server.service.StatusService;

/**
 * An AWS lambda function that logs a user in and returns the user object and an auth code for
 * a successful login.
 */
public class GetFeedHandler implements RequestHandler<PagedRequest<Status>, GetFeedResponse> {
    @Override
    public GetFeedResponse handleRequest(PagedRequest<Status> getFeedRequest, Context context) {
        return new StatusService().getFeed(getFeedRequest);
    }
}