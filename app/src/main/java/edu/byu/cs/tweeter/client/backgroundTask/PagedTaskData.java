package edu.byu.cs.tweeter.client.backgroundTask;

import edu.byu.cs.tweeter.client.presenter.AbstractPresenters.PagedPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class PagedTaskData<T> {
    private AuthToken token;
    private User user;
    private int pageSize;
    private T lastItem;
    private PagedPresenter<T>.PagedObserver observer;

    public PagedTaskData(AuthToken token, User user, int pageSize, T lastItem, PagedPresenter<T>.PagedObserver observer) {
        this.token = token;
        this.user = user;
        this.pageSize = pageSize;
        this.lastItem = lastItem;
        this.observer = observer;
    }

    public AuthToken getToken() { return token; }
    public User getUser() { return user; }
    public int getPageSize() { return pageSize; }
    public T getLastItem() { return lastItem; }
    public PagedPresenter<T>.PagedObserver getObserver() { return observer; }
}
