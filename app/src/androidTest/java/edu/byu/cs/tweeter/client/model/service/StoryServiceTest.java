package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.backgroundTask.PagedTaskData;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class StoryServiceTest {
    private User currentUser;
    private AuthToken currentAuthToken;

    private StoryService storyServiceSpy;
    private StoryServiceObserver observerSpy;

    private CountDownLatch countDownLatch;

    /**
     * Create a FollowService spy that uses a mock ServerFacade to return known responses to
     * requests.
     */
    @BeforeEach
    public void setup() {
        currentUser = new User("FirstName", "LastName", null);
        currentAuthToken = new AuthToken();

        storyServiceSpy = Mockito.spy(new StoryService());

        // Setup an observer for the FollowService
        observerSpy = Mockito.spy(new StoryServiceObserver());

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    /**
     * A {@link FollowService.PagedObserver} implementation that can be used to get the values
     * eventually returned by an asynchronous call on the {@link FollowService}. Counts down
     * on the countDownLatch so tests can wait for the background thread to call a method on the
     * observer.
     */
    private class StoryServiceObserver implements Service.PagedObserver<Status> {

        private boolean success;
        private String message;
        private List<Status> statuses;
        private boolean hasMorePages;

        @Override
        public void pagedTaskSuccess(List<Status> items, boolean morePages) {
            this.success = true;
            this.message = null;
            this.statuses = items;
            this.hasMorePages = morePages;

            countDownLatch.countDown();
        }

        @Override
        public void taskFailed(String message) {
            this.success = false;
            this.message = message;
            this.statuses = null;
            this.hasMorePages = false;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }
    }

    /**
     * Verify that for successful requests, the {@link FollowService#loadMoreFollowing}
     * asynchronous method eventually returns the same result as the {@link ServerFacade}.
     */
    @Test
    public void testGetFollowees_validRequest_correctResponse() throws InterruptedException {
        PagedTaskData<Status> data = new PagedTaskData<>(currentAuthToken, currentUser, 3, null, observerSpy);
        storyServiceSpy.getStory(data);
        awaitCountDownLatch();

        List<Status> expectedStatuses = FakeData.getInstance().getFakeStatuses().subList(0, 3);
        Assertions.assertTrue(observerSpy.isSuccess());
        Assertions.assertNull(observerSpy.getMessage());
        Assertions.assertEquals(expectedStatuses, observerSpy.getStatuses());
        Assertions.assertTrue(observerSpy.getHasMorePages());
        Mockito.verify(storyServiceSpy).getStory(data);
        Mockito.verify(observerSpy).pagedTaskSuccess(observerSpy.getStatuses(), observerSpy.getHasMorePages());
    }

//    /**
//     * Verify that for successful requests, the the {@link FollowService#loadMoreFollowing(PagedTaskData)}
//     * method loads the profile image of each user included in the result.
//     */
//    @Test
//    public void testGetFollowees_validRequest_loadsProfileImages() throws InterruptedException {
//        PagedTaskData<User> data = new PagedTaskData<>(currentAuthToken, currentUser, 3, null, observer);
//        storyServiceSpy.loadMoreFollowing(data);
//        awaitCountDownLatch();
//
//        List<User> followees = observer.getStatuses();
//        Assertions.assertTrue(followees.size() > 0);
//    }

//    /**
//     * Verify that for unsuccessful requests, the the {@link FollowService#loadMoreFollowing(PagedTaskData)}
//     * method returns the same failure response as the server facade.
//     */
//    @Test
//    public void testGetFollowees_invalidRequest_returnsNoFollowees() throws InterruptedException {
//        PagedTaskData<User> data = new PagedTaskData<>(null, null, 0, null, observer);
//        storyServiceSpy.loadMoreFollowing(data);
//        awaitCountDownLatch();
//
//        Assertions.assertFalse(observer.isSuccess());
//        Assertions.assertEquals("Failed to get following because of exception: [BadRequest] Request needs to have a follower alias", observer.getMessage());//.substring(0, 175));
//        Assertions.assertNull(observer.getStatuses());
//        Assertions.assertFalse(observer.getHasMorePages());
//    }
}