package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {
    public StoryPresenter(PagedView<Status> inView) { super(inView); }

    public void initiateGetStory(User user) {
        loadMoreItems();

        new StoryService().getStory(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new PagedObserver());
    }
    public void initiateGetUser(String username) {
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), username, new GetUserObserver());
    }
}
