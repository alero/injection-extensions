package com.hrodberaht.inject.extension.transaction.manager;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.internal.AspectJTransactionHandler;
import org.aspectj.lang.Aspects;
import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.InjectionRegisterBase;
import org.hrodberaht.inject.register.RegistrationModule;
import org.hrodberaht.inject.register.RegistrationModuleAnnotation;

import java.util.Collection;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-11 21:09:52
 * @version 1.0
 * @since 1.0
 */
public class RegistrationTransactionManager extends RegistrationModuleAnnotation implements RegistrationModule {

    RegistrationModuleAnnotation registration = null;
    InjectContainer theContainer = null;

    public RegistrationTransactionManager(final TransactionManager transactionManager
            , InjectionRegisterBase register) {
        registration = new RegistrationModuleAnnotation(){
            @Override
            public void registrations() {
                // the withInstance will automatically create a singleton with that instance
                register(TransactionManager.class).withInstance(transactionManager);
                // This will make the registration to the correct implementation as well
                // for usage in the application that needs to know the implementation
                register(transactionManager.getClass()).withInstance(transactionManager);
            }
        };
        registration.registrations();
        theContainer = register.getInjectContainer();
    }

    @Override
    public void postRegistration() {
        AspectJTransactionHandler aspectJTransactionHandler = Aspects.aspectOf(AspectJTransactionHandler.class);
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
