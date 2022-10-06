package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.presenter.FeedPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService extends Service {
    public void getFeed(AuthToken authToken, User user, int pageSize, Status lastStatus, FeedPresenter.PagedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(authToken, user, pageSize, lastStatus, new GetFeedHandler(observer));
        executeTask(getFeedTask);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private class GetFeedHandler extends PagedTaskHandler<FeedPresenter.PagedObserver, Status> {
        public GetFeedHandler(FeedPresenter.PagedObserver inObs) { super(inObs); }

        @Override
        protected void handleFailureMessage(FeedPresenter.PagedObserver observer, String message) {
            observer.taskFailed("Failed to get feed: " + message);
        }

        @Override
        protected void handleExceptionMessage(FeedPresenter.PagedObserver observer, String message) {
            observer.taskFailed("Failed to get feed because of exception: " + message);
        }
    }
}
