package edu.byu.cs.tweeter.client.presenter;

public abstract class Presenter {
    public interface View {}

    protected View mView;

    public Presenter(View inView) { mView = inView; }
}
