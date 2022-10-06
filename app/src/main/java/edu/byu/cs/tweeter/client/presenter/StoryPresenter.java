package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {
    public StoryPresenter(PagedView<Status> inView) { super(inView); }

    public void initiateGetStory(User user) {
        loadMoreItems(user);

        new StoryService().getStory(data);
    }
}
