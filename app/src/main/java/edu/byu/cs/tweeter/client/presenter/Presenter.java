package edu.byu.cs.tweeter.client.presenter;

public class Presenter {
    public interface View {}

    protected View mView;

    public Presenter(View inView) { mView = inView; }
}
