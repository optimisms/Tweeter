package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.MainPresenter.MainView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class MainPresenterUnitTest {
    private StatusService mockStatusService;
    private MainView mockView;
    private Status mockStatus;
    private AuthToken mockToken;
    private final String post = "Having a great vacation in Eleuthera!";

    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup() {
        //Create mock dependencies
        mockStatusService = Mockito.mock(StatusService.class);
        mockView = Mockito.mock(MainView.class);
        mockStatus = Mockito.mock(Status.class);
        mockToken = Mockito.mock(AuthToken.class);
        Cache mockCache = Mockito.mock(Cache.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));

        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);
        Mockito.when(mainPresenterSpy.getNewStatus(post)).thenReturn(mockStatus);
        Mockito.when(mockCache.getCurrUserAuthToken()).thenReturn(mockToken);

        Cache.setInstance(mockCache);
    }

    @Test
    public void testPostStatusSuccess() {
        setAnswer(invocation -> {
            assertInputs(invocation);
            getObserver(invocation).taskSuccess();
            return null;
        });

        mainPresenterSpy.initiatePostStatus(post);
        verifyMethods("Successfully Posted!");
    }

    @Test
    public void testPostStatusFailure() {
        setAnswer(invocation -> {
            assertInputs(invocation);
            getObserver(invocation).taskFailed("Failed to post status: Exceeded character limit.");
            return null;
        });

        mainPresenterSpy.initiatePostStatus(post);
        verifyMethods("Failed to post status: Exceeded character limit.");
    }

    @Test
    public void testPostStatusException() {
        setAnswer(invocation -> {
            assertInputs(invocation);
            getObserver(invocation).taskFailed("Failed to post status because of exception: Session expired.");
            return null;
        });

        mainPresenterSpy.initiatePostStatus(post);
        verifyMethods("Failed to post status because of exception: Session expired.");
    }

    private void assertInputs(InvocationOnMock invocation) {
        Assertions.assertNotNull(invocation.getArgument(0, AuthToken.class));
        Assertions.assertEquals(invocation.getArgument(0, AuthToken.class), mockToken);
        Assertions.assertNotNull(invocation.getArgument(1, Status.class));
        Assertions.assertEquals(invocation.getArgument(1, Status.class), mockStatus);
    }

    private MainPresenter.PostStatusObserver getObserver(InvocationOnMock invocation) {
        return invocation.getArgument(2, MainPresenter.PostStatusObserver.class);
    }

    private void setAnswer(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());
    }

    private void verifyMethods(String result) {
        Mockito.verify(mockView).displayMessage("Posting Status...");
        Mockito.verify(mockView).clearMessage();
        Mockito.verify(mockView).displayMessage(result);
    }
}