package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.model.domain.User;

public class PagedPresenter<T> extends Presenter {
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

    public PagedPresenter(PagedView<T> inView) { mView = inView; }

    public boolean isLoading() { return isLoading; }
    public boolean hasMorePages() { return hasMorePages; }

    public void loadMoreItems() {
        isLoading = true;
        mView.setLoadingFooter();
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

}
