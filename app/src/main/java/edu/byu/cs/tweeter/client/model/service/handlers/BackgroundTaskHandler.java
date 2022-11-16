package edu.byu.cs.tweeter.client.model.service.handlers;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.Service;

public abstract class BackgroundTaskHandler<T extends Service.Observer> extends Handler {
    private final T observer;
    private final String goal;

    public BackgroundTaskHandler(T observer, String goal) {
        super(Looper.getMainLooper());
        this.observer = observer;
        this.goal = goal;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY);
        if (success) {
            handleSuccessMessage(observer, msg.getData());
        } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(BackgroundTask.MESSAGE_KEY);
            observer.taskFailed("Failed to " + goal + ": " + message);
        } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(BackgroundTask.EXCEPTION_KEY);
            observer.taskFailed("Failed to " + goal + " because of exception: " + ex.getMessage());
        }
    }

    protected abstract void handleSuccessMessage(T observer, Bundle data);
}
