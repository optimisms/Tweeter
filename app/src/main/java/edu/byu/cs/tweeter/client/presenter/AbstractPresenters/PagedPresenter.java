package edu.byu.cs.tweeter.client.presenter.AbstractPresenters;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.PagedTaskData;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter {
    public interface PagedView<T> extends View {
        void setLoadingFooter();
        void addItems(List<T> items);
        void displayMessage(String message);
        void clearMessage();
        void startUserActivity(User user);
    }

    protected static final int PAGE_SIZE = 10;

    protected T lastItem;
    protected boolean hasMorePages;
    protected boolean isLoading = false;

    protected PagedTaskData<T> data;

    public PagedPresenter(PagedView<T> inView) { super(inView); }

    public boolean isLoading() { return isLoading; }
    public boolean hasMorePages() { return hasMorePages; }

    public void loadMoreItems(User user) {
        isLoading = true;
        ((PagedView<T>) mView).setLoadingFooter();

        data = new PagedTaskData<>(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new PagedObserver());
        callServiceMethod();
    }

    public abstract void callServiceMethod();

    public void initiateGetUser(String username) {
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), username, new GetUserObserver());
    }

    public class PagedObserver implements Service.PagedObserver<T> {
        @Override
        public void pagedTaskSuccess(List<T> items, boolean morePages) {
            isLoading = false;
            ((PagedView<T>) mView).setLoadingFooter();

            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            hasMorePages = morePages;
            ((PagedView<T>) mView).addItems(items);
        }

        @Override
        public void taskFailed(String message) {
            isLoading = false;
            ((PagedView<T>) mView).setLoadingFooter();

            ((PagedView<T>) mView).clearMessage();
            ((PagedView<T>) mView).displayMessage(message);
        }
    }

    public class GetUserObserver implements UserService.GetUserObserver {
        @Override
        public void getUserSuccess(User user) {
            ((PagedView<T>) mView).displayMessage("Getting user's profile...");
            ((PagedView<T>) mView).startUserActivity(user);
        }

        @Override
        public void taskFailed(String message) {
            ((PagedView<T>) mView).clearMessage();
            ((PagedView<T>) mView).displayMessage(message);
        }
    }
}
