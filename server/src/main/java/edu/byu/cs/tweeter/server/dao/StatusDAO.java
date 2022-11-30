package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusDAO {

//    public Integer getStorySize(User poster) {
//        // TODO: uses the dummy data.  Replace with a real implementation.
//        assert poster != null;
//        return getDummyStory().size();
//    }
//    public Integer getFeedSize(User viewer) {
//        // TODO: uses the dummy data.  Replace with a real implementation.
//        assert viewer != null;
//        return getDummyFeed().size();
//    }

    public GetStoryResponse getStory(PagedRequest<Status> request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getTargetUserAlias() != null;

        List<Status> allStatuses = getDummyStory();
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                Status lastStatus = request.getLastItem() == null ? null : request.getLastItem();
                int storyIndex = getStoryStartingIndex(lastStatus, allStatuses);

                for(int limitCounter = 0; storyIndex < allStatuses.size() && limitCounter < request.getLimit(); storyIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(storyIndex));
                }

                hasMorePages = storyIndex < allStatuses.size();
            }
        }

        return new GetStoryResponse(responseStatuses, hasMorePages);
    }
    public GetFeedResponse getFeed(PagedRequest<Status> request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getTargetUserAlias() != null;

        List<Status> allStatuses = getDummyFeed();
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                Status lastStatus = request.getLastItem() == null ? null : request.getLastItem();
                int feedIndex = getFeedStartingIndex(lastStatus, allStatuses);

                for(int limitCounter = 0; feedIndex < allStatuses.size() && limitCounter < request.getLimit(); feedIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(feedIndex));
                }

                hasMorePages = feedIndex < allStatuses.size();
            }
        }

        return new GetFeedResponse(responseStatuses, hasMorePages);
    }

    private int getStoryStartingIndex(Status lastStatus, List<Status> allStatuses) {
        int storyIndex = 0;

        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatus.equals(allStatuses.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    storyIndex = i + 1;
                    break;
                }
            }
        }

        return storyIndex;
    }
    private int getFeedStartingIndex(Status lastStatus, List<Status> allStatuses) {
        int feedIndex = 0;

        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatus.equals(allStatuses.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    feedIndex = i + 1;
                    break;
                }
            }
        }

        return feedIndex;
    }

    List<Status> getDummyFeed() {
        return getFakeData().getFakeStatuses();
    }
    List<Status> getDummyStory() {
        return getFakeData().getFakeStatuses();
    }

    FakeData getFakeData() {
        return FakeData.getInstance();
    }

}



