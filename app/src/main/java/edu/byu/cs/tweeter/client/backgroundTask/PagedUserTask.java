package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedUserTask extends PagedTask<User> {
    protected PagedUserTask(PagedTaskData<User> data, Handler messageHandler) { super (data, messageHandler); }

    @Override
    protected final List<User> getUsersForItems(List<User> items) {
        return items;
    }
}
