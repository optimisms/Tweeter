package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FeedService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {
    private static final String LOG_TAG = "GetFeedTask";

    public GetFeedTask(PagedTaskData<Status> data, Handler messageHandler) { super(data, messageHandler); }

    @Override
    protected PagedResponse<Status> getResponse() throws IOException, TweeterRemoteException {
        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        PagedRequest<Status> req = new PagedRequest<>(getAuthToken(), targetUserAlias, getLimit(), getLastItem());
        return getServerFacade().getFeed(req, FeedService.GET_FEED_URL_PATH);
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }
}
