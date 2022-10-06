package edu.byu.cs.tweeter.client.presenter.AbstractPresenters;

public abstract class Presenter {
    public interface View {}

    protected View mView;

    public Presenter(View inView) { mView = inView; }
}
