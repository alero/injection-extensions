package com.hrodberaht.inject.extension.transaction.manager;

import com.hrodberaht.inject.extension.jdbc.InsertOrUpdater;
import com.hrodberaht.inject.extension.jdbc.JDBCService;
import com.hrodberaht.inject.extension.jdbc.internal.InsertOrUpdaterImpl;
import com.hrodberaht.inject.extension.jdbc.internal.JDBCServiceImpl;
import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.impl.jdbc.TransactionManagerJDBC;
import com.hrodberaht.inject.extension.transaction.manager.impl.jdbc.TransactionManagerJDBCImpl;
import com.hrodberaht.inject.extension.transaction.manager.impl.jpa.TransactionManagerJPA;
import com.hrodberaht.inject.extension.transaction.manager.impl.jpa.TransactionManagerJPAImpl;
import com.hrodberaht.inject.extension.transaction.manager.internal.vendor.ProviderFactory;
import com.hrodberaht.inject.extension.transaction.manager.internal.vendor.ProviderService;
import org.hrodberaht.inject.register.ExtendedModule;
import org.hrodberaht.inject.register.InjectionFactory;
import org.hrodberaht.inject.register.RegistrationModuleAnnotation;

import javax.persistence.EntityManager;
import java.sql.Connection;

/**
 * Injection Transaction Extension
 *
 * @author Robert Alexandersson
 *         2010-aug-11 21:09:52
 * @version 1.0
 * @since 1.0
 */
public class TransactionManagerModule extends ExtendedModule {
    

    public TransactionManagerModule(final TransactionManager transactionManager) {
        registration = new RegistrationModuleAnnotation() {
            @Override
            public void registrations() {
                // the withInstance will automatically create a singleton with that instance
                register(TransactionManager.class).withInstance(transactionManager);

                // This will make the registration to the specific implementation interface as well
                // for usage in an application that needs implementation specific information
                register(getInterface(transactionManager)).withInstance(transactionManager);

                if (transactionManager instanceof InjectionFactory) {
                    InjectionFactory injectionFactory = (InjectionFactory) transactionManager;
                    register(injectionFactory.getInstanceType()).withFactory(injectionFactory);
                }

                if(transactionManager instanceof TransactionManagerJPA){
                    final TransactionManagerJPA transactionManagerJPA = (TransactionManagerJPA)transactionManager;
                    InjectionFactory<Connection> injectionFactory = new InjectionFactory<Connection>(){
                        public Connection getInstance() {
                            return getConnection(transactionManagerJPA);
                        }

                        public Class getInstanceType() {
                            return Connection.class;
                        }
                    };
                    register(Connection.class).withFactory(injectionFactory);
                }

                // Configure the JDBC Services
                register(JDBCService.class).with(JDBCServiceImpl.class);
                register(InsertOrUpdater.class).with(InsertOrUpdaterImpl.class);
            }


        };
        registration.registrations();
    }

    private Connection getConnection(TransactionManagerJPA transactionManagerJPA) {
        EntityManager entityManager = transactionManagerJPA.getNativeManager();
        ProviderService providerService = ProviderFactory.getService(entityManager);
        if(providerService != null){
            return providerService.findConnection(entityManager);   
        }
        throw new RuntimeException("Cannot find Connection for entity-manager "+entityManager);
    }

    private Class getInterface(TransactionManager transactionManager) {
        if (transactionManager instanceof TransactionManagerJPAImpl) {
            return TransactionManagerJPA.class;
        } else if (transactionManager instanceof TransactionManagerJDBCImpl) {
            return TransactionManagerJDBC.class;
        }
        throw new IllegalArgumentException("transactionManager does not have a specific interface");
    }    

    


}
