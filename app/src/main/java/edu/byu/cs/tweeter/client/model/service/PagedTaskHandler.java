package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.PagedTask;

public abstract class PagedTaskHandler<S extends Service.PagedObserver<T>, T> extends BackgroundTaskHandler<S> {

    public PagedTaskHandler(S observer) { super(observer); }

    protected void handleSuccessMessage(S observer, Bundle data) {
        List<T> items = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);
        observer.pagedTaskSuccess(items, hasMorePages);
    }
}
