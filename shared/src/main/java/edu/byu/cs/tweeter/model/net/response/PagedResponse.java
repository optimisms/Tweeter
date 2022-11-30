package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

/**
 * A response that can indicate whether there is more data available from the server.
 */
public class PagedResponse<T> extends Response {

    private final boolean hasMorePages;
    private List<T> items;

    public PagedResponse(List<T> items, boolean hasMorePages) {
        super(true);
        this.hasMorePages = hasMorePages;
        this.items = items;
    }

    PagedResponse(String message) {
        super(false, message);
        hasMorePages = false;
    }

    /**
     * An indicator of whether more data is available from the server. A value of true indicates
     * that the result was limited by a maximum value in the request and an additional request
     * would return additional data.
     *
     * @return true if more data is available; otherwise, false.
     */
    public boolean getHasMorePages() {
        return hasMorePages;
    }

    /**
     * Returns the followers for the corresponding request.
     *
     * @return the followers.
     */
    public List<T> getItems() {
        return items;
    }

//    @Override
//    public boolean equals(Object param) {
//        if (this == param) {
//            return true;
//        }
//
//        if (param == null || getClass() != param.getClass()) {
//            return false;
//        }
//
//        PagedResponse<T> that = (PagedResponse<T>) param;
//
//        return (Objects.equals(items, that.items) &&
//                Objects.equals(this.getMessage(), that.getMessage()) &&
//                this.isSuccess() == that.isSuccess());
//    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }
}
