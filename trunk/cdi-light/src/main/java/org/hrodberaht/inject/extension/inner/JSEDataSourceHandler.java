package org.hrodberaht.inject.extension.inner;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: alexbrob
 * Date: 2012-12-04
 * Time: 07:57
 * To change this template use File | Settings | File Templates.
 */
public class JSEDataSourceHandler implements DataSource {





    private String dataSourceName;

    public JSEDataSourceHandler(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Connection getConnection() throws SQLException {
        return DBPool.getConnection(dataSourceName);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return DBPool.getConnection(username, password, dataSourceName);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return new PrintWriter(System.out);
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
