package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.Status;

public abstract class PagedStatusTask extends PagedTask<Status> {
    protected PagedStatusTask(PagedTaskData<Status> data, Handler messageHandler) { super(data, messageHandler); }
}
