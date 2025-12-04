package com.mycompany.argomachinemanagement.src.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 * @param <T>
 */
public interface IDao<T> {

    List<T> findAll();

    boolean update(T t);

    boolean delete(T t);

    int insert(T t);

    T getFromResultSet(ResultSet resultSet) throws SQLException;
}


