package test.com.hrodberaht.inject.extension.transaction;

import com.hrodberaht.inject.extension.transaction.junit.TransactionContainerCreator;
import com.hrodberaht.inject.extension.transaction.manager.RegistrationTransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.impl.JPATransactionManager;
import org.hrodberaht.inject.Container;
import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.InjectionRegisterModule;
import test.com.hrodberaht.inject.extension.transaction.example.JPATransactedApplication;

import javax.persistence.Persistence;

public class ModuleContainerForTests implements TransactionContainerCreator {
    public ModuleContainerForTests() {
    }

    public InjectContainer createContainer() {
        InjectionRegisterModule register = new InjectionRegisterModule();
        register.activateContainerJavaXInject();
        register.register(JPATransactedApplication.class);
        // Create the JPA transaction manager, different managers will need different objects in their construct.
        final JPATransactionManager transactionManager =
                new JPATransactionManager(Persistence.createEntityManagerFactory("example-jpa"));
        // Use the special RegistrationModule named TransactionManager,
        // this registers all needed for the container and the service
        // and does a setup for the AspectJTransactionHandler.
        register.register(new RegistrationTransactionManager(transactionManager, register));
        
        return register.getInjectContainer();
    }
}