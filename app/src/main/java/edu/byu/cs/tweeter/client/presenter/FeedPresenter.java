package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FeedService;
import edu.byu.cs.tweeter.model.domain.Status;

public class FeedPresenter extends PagedPresenter<Status> {
    public FeedPresenter(PagedView<Status> inView) { super(inView); }

    @Override
    public void callServiceMethod() {
        new FeedService().getFeed(data);
    }
}
