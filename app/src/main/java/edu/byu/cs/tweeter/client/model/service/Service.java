package edu.byu.cs.tweeter.client.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service {
    //TODO: try to refactor execute into a template method where the Service class decides what Task is returned

    public interface Observer {
        void taskFailed(String message);
    }

    public interface FollowButtonObserver extends Observer {
        void enableButton();
    }

    public interface PagedObserver<T> extends Observer {
        void pagedTaskSuccess(List<T> items, boolean morePages);
    }

    public <T extends Runnable> void executeTask(T task) {
        List<T> list = new ArrayList<>(List.of(task));
        executeTasks(Executors.newSingleThreadExecutor(), list);

        //executor.execute(task);
    }
    public <T extends Runnable> void executeTasks(ExecutorService executor, List<T> tasks) {
        //ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
        for (T task : tasks) {
            executor.execute(task);
        }
    }
}
