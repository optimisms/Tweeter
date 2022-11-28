package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.backgroundTask.PagedTaskData;
import edu.byu.cs.tweeter.client.model.service.Service.PagedObserver;
import edu.byu.cs.tweeter.client.presenter.AbstractPresenters.PagedPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class TestPagedTaskData<T> extends PagedTaskData<T> {
    PagedObserver<T> testObserver;

    public TestPagedTaskData(AuthToken token, User user, int pageSize, T lastItem, PagedObserver<T> observer) {
        super(token, user, pageSize, lastItem, null);
        this.testObserver = observer;
    }

    @Override
    public PagedPresenter<T>.PagedObserver getObserver() { throw new RuntimeException("Shouldn't be accessing real Observer in test."); }

    public PagedObserver<T> getTestObserver() {
        return testObserver;
    }
}
