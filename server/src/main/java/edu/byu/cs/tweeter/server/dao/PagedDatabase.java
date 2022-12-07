package edu.byu.cs.tweeter.server.dao;

import java.util.List;

public interface PagedDatabase<T, U> extends Database<T> {
    List<U> getPages(String partition_key, int pageSize, String sort_start_key, String taskType) throws DataAccessException;
}
