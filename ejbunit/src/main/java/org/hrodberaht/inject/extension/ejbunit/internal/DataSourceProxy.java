package org.hrodberaht.inject.extension.ejbunit.internal;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 22:34:53
 * @version 1.0
 * @since 1.0
 */
public class DataSourceProxy implements DataSource {
    private final ThreadLocal<ConnectionHandler> threadLocal = new ThreadLocal<ConnectionHandler>();

    public String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    public String JDBC_URL = "jdbc:hsqldb:mem:";
    public String JDBC_USERNAME = "sa";
    public String JDBC_PASSWORD = "";

    private String dbName = null;

    public DataSourceProxy(String dbName) {
        this.dbName = dbName;
    }

    public void clearDataSource() {
        ConnectionHandler connection = threadLocal.get();
        if (connection != null) {
            try {
                connection.conn.rollback();
                connection.conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            threadLocal.remove();
        }
    }

    public void commitDataSource() {
        ConnectionHandler connection = threadLocal.get();
        if (connection != null) {
            try {
                connection.conn.commit();
                connection.conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            threadLocal.remove();
        }
    }




    public Connection getConnection() throws SQLException {
        ConnectionHandler connection = threadLocal.get();
        if (connection != null) {
            return connection.proxy;
        }
        try {
            Class.forName(JDBC_DRIVER);
            final Connection conn = DriverManager.getConnection(JDBC_URL + dbName, JDBC_USERNAME, JDBC_PASSWORD);
            conn.setAutoCommit(false);
            InvocationHandler invocationHandler = new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("close")) {
                        // do nothing
                        return null;
                    } else if (method.getName().equals("commit")) {
                        // do nothing
                        return null;
                    }
                    return method.invoke(conn, args);
                }
            };

            Connection proxy = (Connection) Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(), new Class[]{Connection.class}, invocationHandler
            );
            threadLocal.set(new ConnectionHandler(conn, proxy));
            return proxy;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    public PrintWriter getLogWriter() throws SQLException {
        return new PrintWriter(System.out);
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 600;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }


    private class ConnectionHandler {
        private Connection conn;
        private Connection proxy;

        private ConnectionHandler(Connection conn, Connection proxy) {
            this.conn = conn;
            this.proxy = proxy;
        }
    }
}
