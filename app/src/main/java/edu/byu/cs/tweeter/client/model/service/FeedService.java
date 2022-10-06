package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.presenter.FeedPresenter.GetFeedObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService extends Service {
    public void getFeed(AuthToken authToken, User user, int pageSize, Status lastStatus, GetFeedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(authToken, user, pageSize, lastStatus, new GetFeedHandler(observer));
        executeTask(getFeedTask);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private class GetFeedHandler extends PagedTaskHandler<GetFeedObserver, Status> {
        public GetFeedHandler(GetFeedObserver inObs) { super(inObs); }

        @Override
        protected void handleFailureMessage(GetFeedObserver observer, String message) {
            observer.taskFailed("Failed to get feed: " + message);
        }

        @Override
        protected void handleExceptionMessage(GetFeedObserver observer, String message) {
            observer.taskFailed("Failed to get feed because of exception: " + message);
        }
    }
}
