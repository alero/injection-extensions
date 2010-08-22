package test.com.hrodberaht.inject.extension.transaction.example;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.junit.InjectionContainerCreator;
import com.hrodberaht.inject.extension.transaction.manager.TransactionManagerModule;
import com.hrodberaht.inject.extension.transaction.manager.impl.jdbc.TransactionManagerJDBCImpl;
import com.hrodberaht.inject.extension.transaction.manager.impl.jpa.TransactionManagerJPAImpl;
import org.hibernate.SessionFactory;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.InjectionRegisterModule;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Injection Transaction Extension
 *
 * @author Robert Alexandersson
 *         2010-aug-11 22:58:13
 * @version 1.0
 * @since 1.0
 */
public class ModuleContainerForJDBCTests implements InjectionContainerCreator {

    public static InjectContainer container;

    public ModuleContainerForJDBCTests() {
    }

    public InjectContainer createContainer() {
        InjectionRegisterModule register = new InjectionRegisterModule();
        register.activateContainerJavaXInject();

        // This is just done to simplify the test application
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("example-jpa");
        EntityManagerFactoryImpl managerFactory = (EntityManagerFactoryImpl)entityManagerFactory;
        DataSource dataSource = getDataSource(managerFactory.getSessionFactory());

        register.register(TransactedApplication.class,  JDBCTransactedApplication.class);
        // Create the JPA transaction manager, different managers will need different objects in their construct.
        TransactionManager transactionManager = new TransactionManagerJDBCImpl(dataSource);
        // Use the special RegistrationModule named TransactionManager,
        // this registers all needed for the container and the service
        // and does a setup for the AspectJTransactionHandler.
        register.register(new TransactionManagerModule(transactionManager, register));
        InjectContainer injectContainer = register.getInjectContainer();
        container = injectContainer;
        return injectContainer;
    }


    public static DataSource getDataSource(SessionFactory sessionFactory) {
		if (sessionFactory instanceof SessionFactoryImplementor) {
			final ConnectionProvider cp = ((SessionFactoryImplementor) sessionFactory).getConnectionProvider();
            DataSource simpleDataSource = new DataSource (){
                PrintWriter printWriter = null;
                public Connection getConnection() throws SQLException {
                    return cp.getConnection();
                }

                public Connection getConnection(String username, String password) throws SQLException {
                    return cp.getConnection();
                }

                public PrintWriter getLogWriter() throws SQLException {
                    return printWriter;
                }

                public void setLogWriter(PrintWriter out) throws SQLException {
                    printWriter = out;
                }

                public void setLoginTimeout(int seconds) throws SQLException {

                }

                public int getLoginTimeout() throws SQLException {
                    return 600;
                }

                public <T> T unwrap(Class<T> iface) throws SQLException {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }

                public boolean isWrapperFor(Class<?> iface) throws SQLException {
                    return false;
                }
            };
            return simpleDataSource;
		}
		return null;
	}
}