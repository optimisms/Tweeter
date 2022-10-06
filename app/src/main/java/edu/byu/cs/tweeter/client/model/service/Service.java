package edu.byu.cs.tweeter.client.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class Service {
    //TODO: try to refactor execute into a template method where the Service class decides what Task is returned

    public interface Observer {
        void taskFailed(String message);
    }

    public interface PagedObserver<T> extends Observer {
        void pagedTaskSuccess(List<T> items, boolean morePages);
    }

    public interface GetCountObserver extends Observer {
        void getCountSuccess(int count);
    }

    public interface AuthObserver extends Observer {
        void authSuccess(User user, AuthToken authToken);
    }

    public interface NoDataReturnedObserver extends Observer {
        void taskSuccess();
    }

    public interface FollowButtonObserver extends NoDataReturnedObserver {
        void enableButton();
    }

    public <T extends Runnable> void executeTask(T task) {
        List<T> list = new ArrayList<>(List.of(task));
        executeTasks(Executors.newSingleThreadExecutor(), list);
    }
    public <T extends Runnable> void executeTasks(ExecutorService executor, List<T> tasks) {
        for (T task : tasks) {
            executor.execute(task);
        }
    }
}
