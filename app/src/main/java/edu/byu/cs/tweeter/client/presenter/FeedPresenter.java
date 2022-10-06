package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FeedService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> {
    public FeedPresenter(PagedView<Status> inView) { super(inView); }

    public void initiateGetFeed(User user) {
        loadMoreItems(user);

        new FeedService().getFeed(data);
    }
}
