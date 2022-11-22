package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {
    public GetStoryTask(PagedTaskData<Status> data, Handler messageHandler) { super(data, messageHandler); }

    @Override
    protected PagedResponse<Status> getResponse() throws IOException, TweeterRemoteException {
        return null;
    }

//    @Override
//    protected Pair<List<Status>, Boolean> getItems() {
//        return getFakeData().getPageOfStatus(getLastItem(), getLimit());
//    }
}
