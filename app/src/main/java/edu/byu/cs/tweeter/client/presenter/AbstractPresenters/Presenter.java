package edu.byu.cs.tweeter.client.presenter.AbstractPresenters;

public abstract class Presenter {
    public interface View {
        void displayMessage(String message);
        void clearMessage();
    }

    protected View mView;

    public Presenter(View inView) { mView = inView; }
}
