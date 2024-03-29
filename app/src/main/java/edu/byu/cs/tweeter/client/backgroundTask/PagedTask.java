package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;

public abstract class PagedTask<T> extends AuthenticatedTask {

    public static final String ITEMS_KEY = "items";
    public static final String MORE_PAGES_KEY = "more-pages";

    /**
     * The user whose items are being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private final User targetUser;

    /**
     * Maximum number of statuses to return (i.e., page size).
     */

    private final int limit;

    /**
     * The last status returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private final T lastItem;

    /**
     * The items returned in the current page of results.
     */
    private List<T> items;

    /**
     * Indicates whether there are more pages of items that can be retrieved on subsequent calls.
     */
    private boolean hasMorePages;

    protected PagedTask(PagedTaskData<T> data, Handler messageHandler) {
        super(data.getToken(), messageHandler);
        this.targetUser = data.getUser();
        this.limit = data.getPageSize();
        this.lastItem = data.getLastItem();
    }

    protected User getTargetUser() {
        return targetUser;
    }

    protected int getLimit() {
        return limit;
    }

    protected T getLastItem() {
        return lastItem;
    }

    @Override
    protected final void runTask() throws IOException {
        try {
            PagedResponse<T> resp = getResponse();

            if (resp.isSuccess()) {
                items = resp.getItems();
                hasMorePages = resp.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(resp.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            ex.printStackTrace();
            sendExceptionMessage(ex);
        }
    }

    protected abstract PagedResponse<T> getResponse() throws IOException, TweeterRemoteException;

    @Override
    protected final void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(ITEMS_KEY, (Serializable) items);
        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
    }

    protected abstract String getLogTag();
}
