package com.hrodberaht.inject.extension.transaction.manager;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.jdbc.InsertOrUpdater;
import com.hrodberaht.inject.extension.transaction.jdbc.InsertOrUpdaterImpl;
import com.hrodberaht.inject.extension.transaction.jdbc.JDBCService;
import com.hrodberaht.inject.extension.transaction.jdbc.JDBCServiceImpl;
import com.hrodberaht.inject.extension.transaction.manager.impl.jdbc.TransactionManagerJDBC;
import com.hrodberaht.inject.extension.transaction.manager.impl.jdbc.TransactionManagerJDBCImpl;
import com.hrodberaht.inject.extension.transaction.manager.impl.jpa.TransactionManagerJPA;
import com.hrodberaht.inject.extension.transaction.manager.impl.jpa.TransactionManagerJPAImpl;
import com.hrodberaht.inject.extension.transaction.manager.internal.AspectJTransactionHandler;
import org.aspectj.lang.Aspects;
import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.InjectionRegisterBase;
import org.hrodberaht.inject.register.InjectionFactory;
import org.hrodberaht.inject.register.RegistrationModule;
import org.hrodberaht.inject.register.RegistrationModuleAnnotation;

import java.util.Collection;

/**
 * Injection Transaction Extension
 *
 * @author Robert Alexandersson
 *         2010-aug-11 21:09:52
 * @version 1.0
 * @since 1.0
 */
public class TransactionManagerModule extends RegistrationModuleAnnotation implements RegistrationModule {

    RegistrationModuleAnnotation registration = null;
    InjectContainer theContainer = null;

    public TransactionManagerModule(final TransactionManager transactionManager
            , InjectionRegisterBase register) {
        registration = new RegistrationModuleAnnotation() {
            @Override
            public void registrations() {
                // the withInstance will automatically create a singleton with that instance
                register(TransactionManager.class).withInstance(transactionManager);
                // This will make the registration to the correct implementation as well
                // for usage in the application that needs to know the implementation
                register(transactionManager.getClass()).withInstance(transactionManager);

                // This will make the registration to the specific implementation interface as well
                // for usage in an application that needs implementation specific information
                register(getInterface(transactionManager)).withInstance(transactionManager);

                if (transactionManager instanceof InjectionFactory) {
                    InjectionFactory injectionFactory = (InjectionFactory) transactionManager;
                    register(injectionFactory.getInstanceType()).withFactory(injectionFactory);
                }

                if (transactionManager instanceof TransactionManagerJDBCImpl) {
                    // Connect the the JDBC Services
                    register(JDBCService.class).with(JDBCServiceImpl.class);
                    register(InsertOrUpdater.class).with(InsertOrUpdaterImpl.class);
                }

            }


        };
        // Actually performs the registrations to this module
        registration.registrations();
        theContainer = register.getInjectContainer();
    }

    private Class getInterface(TransactionManager transactionManager) {
        if (transactionManager instanceof TransactionManagerJPAImpl) {
            return TransactionManagerJPA.class;
        } else if (transactionManager instanceof TransactionManagerJDBCImpl) {
            return TransactionManagerJDBC.class;
        }
        throw new IllegalArgumentException("transactionManager does not have a specific interface");
    }

    @Override
    public void postRegistration() {
        AspectJTransactionHandler aspectJTransactionHandler = Aspects.aspectOf(AspectJTransactionHandler.class);
        TransactionManager transactionManager = theContainer.get(TransactionManager.class);
        System.out.println("Thread " + Thread.currentThread() +
                " Connecting the aspect " + aspectJTransactionHandler.toString()
                + " to transaction manager " + transactionManager);
        theContainer.injectDependencies(aspectJTransactionHandler);
    }

    public Collection getRegistrations() {
        return registration.getRegistrations();
    }

    @Override
    public void registrations() {
        // just do nothing
    }


}
