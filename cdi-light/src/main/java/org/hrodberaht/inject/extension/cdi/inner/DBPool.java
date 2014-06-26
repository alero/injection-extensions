package org.hrodberaht.inject.extension.cdi.inner;

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

    // protected static final LinkedList<Connection> AVAILABLE_CONNECTIONS = new LinkedList<Connection>();

    private static InheritableThreadLocal<Connection> threadLocal = new InheritableThreadLocal<Connection>();

    private static boolean useThreadReusableConnection = true;

    public static Connection getConnection(String dataSourceName) throws SQLException {
        DataSourceDriver dataSourceDriver = initiateDriver(dataSourceName, null, null);
        return getConnection(dataSourceName, dataSourceDriver);
    }

    public static synchronized Connection  getConnection(
            String username, String password, String dataSourceName) throws SQLException {
        DataSourceDriver dataSourceDriver = initiateDriver(dataSourceName, username, password);
        return getConnection(dataSourceName, dataSourceDriver);
    }

    private static DataSourceDriver initiateDriver(String dataSourceName, String username, String password) {
        String driver = System.getProperty(dataSourceName+".driver");
        if(driver == null || "".equals(driver)){
            throw new IllegalStateException("JSEResourceCreator can not find a driver for "+dataSourceName);
        }
        String url = System.getProperty(dataSourceName+".url");
        username = username != null ? username : System.getProperty(dataSourceName+".username");
        password = password != null ? password : System.getProperty(dataSourceName+".password");
        return new DataSourceDriver(driver, url, username, password);
    }


    private static synchronized Connection  getConnection(
            String dataSourceName, DataSourceDriver dataSourceDriver) throws SQLException {
        if(useThreadReusableConnection && threadLocal.get() != null){
            Connection connection = threadLocal.get();
            System.out.println("Reused Connection "+connection.toString());
            return connection;
        }

        Connection connection = createConnectionProxy(dataSourceDriver);

        if(useThreadReusableConnection){
            System.out.println("Create Reusable Connection "+connection.toString());
            threadLocal.set(connection);
        }else{
            System.out.println("Create Connection "+connection.toString());
        }
        return connection;


    }

    private static Connection createConnectionProxy(
            DataSourceDriver dataSourceDriver)
            throws SQLException {
        try {
            Class.forName(dataSourceDriver.driver);
            final Connection conn = DriverManager.getConnection(
                    dataSourceDriver.url, dataSourceDriver.username, dataSourceDriver.password);
            conn.setAutoCommit(true);
            if(!useThreadReusableConnection){
                return conn;
            }

            InvocationHandler invocationHandler = new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("close")) {
                        // conn.close();
                        // threadLocal.remove();
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

            // return conn;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static class DataSourceDriver{
        private String driver;
        private String url;
        private String username;
        private String password;

        private DataSourceDriver(String driver, String url, String username, String password) {
            this.driver = driver;
            this.url = url;
            this.username = username;
            this.password = password;
        }
    }

}
