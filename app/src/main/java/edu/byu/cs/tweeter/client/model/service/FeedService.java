package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.presenter.FeedPresenter;
import edu.byu.cs.tweeter.model.domain.Status;

public class FeedService extends Service {
    public void getFeed(PagedTaskData<Status> data) {
        GetFeedTask getFeedTask = new GetFeedTask(data.getToken(), data.getUser(), data.getPageSize(), data.getLastItem(), new GetFeedHandler(data.getObserver()));
        executeTask(getFeedTask);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private class GetFeedHandler extends PagedTaskHandler<FeedPresenter.PagedObserver, Status> {
        public GetFeedHandler(FeedPresenter.PagedObserver inObs) { super(inObs, "get feed"); }
    }
}
