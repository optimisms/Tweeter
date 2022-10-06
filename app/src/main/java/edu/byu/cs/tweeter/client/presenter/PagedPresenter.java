package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public class PagedPresenter extends Presenter {
    public interface PagedView<T> extends View {
        void setLoadingFooter();
        void addItems(List<T> items);
        void displayMessage(String message);
        void clearMessage();
        void startUserActivity(User user);
    }
}
