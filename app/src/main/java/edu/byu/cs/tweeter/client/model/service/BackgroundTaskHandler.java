package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;

public abstract class BackgroundTaskHandler<T extends Service.Observer> extends Handler {
    private final T observer;

    public BackgroundTaskHandler(T observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY);
        if (success) {
            handleSuccessMessage(observer, msg.getData());
        } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(BackgroundTask.MESSAGE_KEY);
            handleFailureMessage(observer, message);
           // observer.handleFailure(message);
        } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(BackgroundTask.EXCEPTION_KEY);
            handleExceptionMessage(observer, ex.getMessage());
           // observer.handleException(ex);
        }
    }

    protected abstract void handleSuccessMessage(T observer, Bundle data);
    protected abstract void handleFailureMessage(T observer, String message);
    protected abstract void handleExceptionMessage(T observer, String message);

//    protected abstract void handleSuccessMessage(Bundle data);
//    protected abstract void handleFailureMessage(String message);
}
