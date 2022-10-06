package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.model.domain.Status;

public class StoryPresenter extends PagedPresenter<Status> {
    public StoryPresenter(PagedView<Status> inView) { super(inView); }

    @Override
    public void callServiceMethod() {
        new StoryService().getStory(data);
    }
}
