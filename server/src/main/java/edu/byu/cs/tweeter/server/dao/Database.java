package edu.byu.cs.tweeter.server.dao;

public interface Database<T> {
    T get();
    void add(T toAdd);
    void update(T toUpdate);
    void delete(T toDelete);
}
