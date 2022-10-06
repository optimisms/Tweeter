package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> {
    public FollowersPresenter(PagedView<User> inView) { super(inView); }

    public void loadMoreFollowers(User user) {
        loadMoreItems(user);

        new FollowService().loadMoreFollowers(data);
    }
}
