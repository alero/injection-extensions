package com.hrodberaht.inject.extension.transaction.jdbc;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.impl.jdbc.TransactionManagerJDBC;

import javax.inject.Inject;
import javax.inject.Provider;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Injection Transaction Extension
 *
 * @author Robert Alexandersson
 *         2010-aug-21 22:14:33
 * @version 1.0
 * @since 1.0
 */
public class JDBCServiceImpl implements JDBCService {

    @Inject
    Provider<InsertOrUpdater> iouProvider;

    @Inject
    Provider<Connection> connectionProvider;

    @Inject
    Provider<TransactionManager> managerProvider;

    public InsertOrUpdater createInsertOrUpdate(String table) {
        InsertOrUpdater _insertOrUpdater = iouProvider.get();
        InsertOrUpdaterImpl insertOrUpdater = (InsertOrUpdaterImpl) _insertOrUpdater;
        return insertOrUpdater.table(table);
    }

    public Insert createInsert(String table) {
        InsertOrUpdater _insertOrUpdater = iouProvider.get();
        InsertOrUpdaterImpl insertOrUpdater = (InsertOrUpdaterImpl) _insertOrUpdater;
        return insertOrUpdater.table(table);
    }

    public int insert(Insert insert) {
        return insertOrUpdate((InsertOrUpdater) insert);
    }

    public int insertOrUpdate(InsertOrUpdater _insertOrUpdater) {
        try {
            if (_insertOrUpdater instanceof InsertOrUpdaterImpl) {
                InsertOrUpdaterImpl insertOrUpdater = (InsertOrUpdaterImpl) _insertOrUpdater;
                Connection connection = connectionProvider.get();
                String sql = insertOrUpdater.getPreparedSql();
                Object[] args = insertOrUpdater.getArgs();

                PreparedStatement pstmt = connection.prepareStatement(sql);
                appendArguments(pstmt, args);
                try {

                    return pstmt.executeUpdate();
                } finally {
                    close(pstmt);
                    // jdbcProvider.get().manuallyClose(connection);
                }

            }
        } catch (SQLException e) {
            throw new JDBCException(e);
        }

        throw new RuntimeException("A custom InsertOrUpdater was used, use InsertOrUpdaterImpl is the instance");

    }


    private void appendArguments(PreparedStatement pstmt, Object[] args) throws SQLException {
        int argumentOrder = 1;
        for (Object argument : args) {
            addArgument(pstmt, argumentOrder, argument);
            argumentOrder++;
        }
    }

    private void addArgument(PreparedStatement pstmt, int argumentOrder, Object argument) throws SQLException {
        if (argument instanceof String) {
            pstmt.setString(argumentOrder, (String) argument);
        } else if (argument instanceof Long) {
            pstmt.setLong(argumentOrder, (Long) argument);
        } else if (argument instanceof Integer) {
            pstmt.setInt(argumentOrder, (Integer) argument);
        } else if (argument instanceof Date) {
            pstmt.setDate(argumentOrder, (Date) argument);
        }

        // TODO: support all data types.
    }


    public <T> Collection<T> query(String sql, RowIterator<T> rowIterator) {

        try {
            Connection connection = connectionProvider.get();
            // Prepared statements?
            Statement pstmt = null;
            ResultSet rs = null;
            Collection<T> queryItems = new ArrayList<T>(50);
            try {
                pstmt = connection.createStatement();
                rs = pstmt.executeQuery(sql);
                int iteration = 0;
                while (rs.next()) {
                    T item = rowIterator.iterate(rs, iteration++);
                    queryItems.add(item);
                }
                return queryItems;
            } finally {
                close(pstmt, rs);
            }
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    public <T> T querySingle(String sql, RowIterator<T> rowIterator) {
        try {
            Connection connection = connectionProvider.get();
            // Prepared statements?
            Statement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = connection.createStatement();
                rs = pstmt.executeQuery(sql);
                if (rs.next()) {
                    if (rs.isLast()) {
                        return rowIterator.iterate(rs, 0);
                    }
                    throw new JDBCException("Multiple returns for a query single");
                } else {
                    return null;
                }
            } finally {
                close(pstmt, rs);
            }
        } catch (SQLException e) {
            throw new JDBCException(e);
        }


    }

    public int execute(String sql) {
        try {
            Connection connection = connectionProvider.get();
            // Prepared statements?
            Statement pstmt = null;
            try {
                pstmt = connection.createStatement();
                return pstmt.executeUpdate(sql);
            } finally {
                close(pstmt);
            }
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }


    private void close(Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
    }

    private void close(Statement stmt) {
        try {
            if(stmt != null){
                stmt.close();
            }
        } catch (SQLException e) {
            // ignore, there is nothing we can do
        }
    }

    private void close(ResultSet rs) {
        try {
            if(rs != null){
                rs.close();
            }
        } catch (SQLException e) {
            // ignore, there is nothing we can do
        }
    }

    private class JDBCException extends RuntimeException {
        public JDBCException(SQLException e) {
            super(e);
        }

        public JDBCException(String message) {
            super(message);
        }
    }
}
