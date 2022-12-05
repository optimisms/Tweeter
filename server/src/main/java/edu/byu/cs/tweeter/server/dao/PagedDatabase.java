package edu.byu.cs.tweeter.server.dao;

import java.util.List;

public interface PagedDatabase<T> extends Database<T> {
    List<T> getPaginated();
}
