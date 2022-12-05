package edu.byu.cs.tweeter.server.dao;

public interface Database<T> {
    T get(String partition, String sort) throws DataAccessException;
    void add(T toAdd) throws DataAccessException;
    void update(T toUpdate) throws DataAccessException;
    void delete(T toDelete) throws DataAccessException;
}
