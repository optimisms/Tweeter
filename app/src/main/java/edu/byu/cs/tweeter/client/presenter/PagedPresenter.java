package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

public class PagedPresenter extends Presenter {
    public interface PagedView<T> extends View {
        void setLoadingFooter();
        void addItems(List<T> items);
    }
}
