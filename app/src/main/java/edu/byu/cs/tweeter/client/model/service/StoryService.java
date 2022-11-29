package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.PagedTaskData;
import edu.byu.cs.tweeter.client.model.service.handlers.PagedTaskHandler;
import edu.byu.cs.tweeter.model.domain.Status;

public class StoryService extends Service {
    public static final String GET_STORY_URL_PATH = "get/story/";

    public void getStory(PagedTaskData<Status> data) {
        GetStoryTask getStoryTask = new GetStoryTask(data, new GetStoryHandler(data.getObserver()));
        executeTask(getStoryTask);
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private class GetStoryHandler extends PagedTaskHandler<Service.PagedObserver<Status>, Status> {
        public GetStoryHandler(Service.PagedObserver<Status> inObs) { super(inObs, "get story"); }
    }
}
