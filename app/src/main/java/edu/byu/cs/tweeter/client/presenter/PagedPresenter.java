package edu.byu.cs.tweeter.client.presenter;

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

    protected PagedView<T> mView;
    protected T lastItem;
    protected boolean hasMorePages;
    protected boolean isLoading = false;

    protected PagedTaskData<T> data;

    public PagedPresenter(PagedView<T> inView) { super(inView); mView = inView; }

    public boolean isLoading() { return isLoading; }
    public boolean hasMorePages() { return hasMorePages; }

    public void loadMoreItems(User user) {
        isLoading = true;
        mView.setLoadingFooter();

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
            mView.setLoadingFooter();

            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            hasMorePages = morePages;
            mView.addItems(items);
        }

        @Override
        public void taskFailed(String message) {
            isLoading = false;
            mView.setLoadingFooter();

            mView.clearMessage();
            mView.displayMessage(message);
        }
    }

    public class GetUserObserver implements UserService.GetUserObserver {
        @Override
        public void getUserSuccess(User user) {
            mView.displayMessage("Getting user's profile...");
            mView.startUserActivity(user);
        }

        @Override
        public void taskFailed(String message) {
            mView.clearMessage();
            mView.displayMessage(message);
        }
    }
}
