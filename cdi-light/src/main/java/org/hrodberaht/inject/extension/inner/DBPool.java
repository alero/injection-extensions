package org.hrodberaht.inject.extension.inner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: alexbrob
 * Date: 2012-12-04
 * Time: 09:28
 * To change this template use File | Settings | File Templates.
 */
public class DBPool {

    protected static final LinkedList<Connection> AVAILABLE_CONNECTIONS = new LinkedList<Connection>();
    // protected static final LinkedList<Connection> USED_CONNECTIONS = new LinkedList<Connection>();

    public static final String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    public static final String JDBC_URL = "jdbc:hsqldb:mem:";
    public static final String JDBC_USERNAME = "sa";
    public static final String JDBC_PASSWORD = "";

    public static Connection getConnection(String dataSourceName) throws SQLException {
        return getConnection(JDBC_USERNAME,JDBC_PASSWORD,  dataSourceName);
    }

    public static synchronized Connection  getConnection(
            String username, String password, String dataSourceName) throws SQLException {
        if(AVAILABLE_CONNECTIONS.isEmpty()){
            Connection connection = createConnectionProxy(username, password, dataSourceName);
            System.out.println("Create Connection "+connection.toString());
            // USED_CONNECTIONS.push(connection);
            return connection;
        }
        Connection connection = AVAILABLE_CONNECTIONS.pop();
        System.out.println("Reuse Connection "+connection.toString());
        return connection;
    }

    private static Connection createConnectionProxy(
            String username, String password, String dataSourceName)
            throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            final Connection conn = DriverManager.getConnection(JDBC_URL + dataSourceName, username, password);
            conn.setAutoCommit(false);
            InvocationHandler invocationHandler = new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("close")) {
                        // do nothing
                        // USED_CONNECTIONS.remove(proxy);
                        AVAILABLE_CONNECTIONS.push((Connection)proxy);
                        System.out.println("Release Connection "+proxy.toString());
                        return null;
                    }

                    return method.invoke(conn, args);
                }
            };

            Connection proxy = (Connection) Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(), new Class[]{Connection.class}, invocationHandler
            );
            return proxy;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
