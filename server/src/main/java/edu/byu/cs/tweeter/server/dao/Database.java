package edu.byu.cs.tweeter.server.dao;

public interface Database<T> {
    T get(String partition) throws DataAccessException;
    T get(String partition, String sort) throws DataAccessException;
    void add(T toAdd);
    void update(T toUpdate);
    void delete(T toDelete);
}
