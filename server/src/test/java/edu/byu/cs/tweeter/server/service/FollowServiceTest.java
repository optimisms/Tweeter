//package edu.byu.cs.tweeter.server.service;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.util.Arrays;
//import java.util.List;
//
//import edu.byu.cs.tweeter.model.domain.AuthToken;
//import edu.byu.cs.tweeter.model.domain.User;
//import edu.byu.cs.tweeter.model.net.request.PagedRequest;
//import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
//import edu.byu.cs.tweeter.server.dao.DataAccessException;
//import edu.byu.cs.tweeter.server.dao.dynamo.FollowDAO;
//
//public class FollowServiceTest {
//
//    private PagedRequest<User> request;
//    private List<User> expectedResponse;
//    private FollowDAO mockFollowDAO;
//    private FollowService followServiceSpy;
//
//    @BeforeEach
//    public void setup() throws DataAccessException {
//        AuthToken authToken = new AuthToken();
//
//        User currentUser = new User("FirstName", "LastName", null);
//
//        User resultUser1 = new User("FirstName1", "LastName1",
//                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
//        User resultUser2 = new User("FirstName2", "LastName2",
//                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
//        User resultUser3 = new User("FirstName3", "LastName3",
//                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
//
//        // Setup a request object to use in the tests
//        request = new PagedRequest<User>(authToken, currentUser.getAlias(), 3, null);
//
//        // Setup a mock FollowDAO that will return known responses
//        expectedResponse = Arrays.asList(resultUser1, resultUser2, resultUser3);
//        mockFollowDAO = Mockito.mock(FollowDAO.class);
//        Mockito.when(mockFollowDAO.getFollowing(request.getTargetUserAlias(), request.getLimit(), null)).thenReturn(expectedResponse);
//
//        followServiceSpy = Mockito.spy(FollowService.class);
//        Mockito.when(followServiceSpy.getFollowDAO()).thenReturn(mockFollowDAO);
//    }
//
//    /**
//     * Verify that the {@link FollowService#getFollowees(PagedRequest)}
//     * method returns the same result as the {@link FollowDAO} class.
//     */
//    @Test
//    public void testGetFollowees_validRequest_correctResponse() {
//        GetFollowingResponse response = followServiceSpy.getFollowees(request);
//        Assertions.assertEquals(expectedResponse, response);
//    }
//}
