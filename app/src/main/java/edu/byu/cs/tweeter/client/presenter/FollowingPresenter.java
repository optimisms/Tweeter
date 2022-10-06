package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> {
    public FollowingPresenter(PagedView<User> inView) { super(inView); }

    public void loadMoreFollowees(User user) {
        loadMoreItems(user);

        new FollowService().loadMoreFollowing(data);
    }
}
