package com.hrodberaht.inject.extension.transaction.manager;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.impl.TransactionManagerJPA;
import com.hrodberaht.inject.extension.transaction.manager.impl.TransactionManagerJPAImpl;
import com.hrodberaht.inject.extension.transaction.manager.internal.AspectJTransactionHandler;
import org.aspectj.lang.Aspects;
import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.InjectionRegisterBase;
import org.hrodberaht.inject.register.InjectionFactory;
import org.hrodberaht.inject.register.RegistrationModule;
import org.hrodberaht.inject.register.RegistrationModuleAnnotation;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * Simple Java Utils
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
        registration = new RegistrationModuleAnnotation(){
            @Override
            public void registrations() {
                // the withInstance will automatically create a singleton with that instance
                register(TransactionManager.class).withInstance(transactionManager);
                // This will make the registration to the correct implementation as well
                // for usage in the application that needs to know the implementation
                register(transactionManager.getClass()).withInstance(transactionManager);

                // This will make the registration to the specific implementation interface as well
                // for usage in the application that has a very limited method set
                register(getInterface(transactionManager)).withInstance(transactionManager);

                if(transactionManager instanceof InjectionFactory){
                    register(EntityManager.class).withFactory((InjectionFactory)transactionManager);
                }
            }


        };
        registration.registrations();
        theContainer = register.getInjectContainer();
    }

    private Class getInterface(TransactionManager transactionManager) {
        if(transactionManager instanceof TransactionManagerJPAImpl){
            return TransactionManagerJPA.class;
        }
        throw new IllegalArgumentException("transactionManager does not have a specific interface");
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
