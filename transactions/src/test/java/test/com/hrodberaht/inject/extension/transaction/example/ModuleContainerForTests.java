package test.com.hrodberaht.inject.extension.transaction.example;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.TransactionManagerModule;
import com.hrodberaht.inject.extension.transaction.manager.impl.TransactionManagerJPAImpl;
import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.InjectionRegisterModule;

import javax.persistence.Persistence;

public class ModuleContainerForTests implements com.hrodberaht.inject.extension.transaction.junit.InjectionContainerCreator {

    public static InjectContainer container;

    public ModuleContainerForTests() {
    }

    public InjectContainer createContainer() {
        InjectionRegisterModule register = new InjectionRegisterModule();
        register.activateContainerJavaXInject();
        register.register(TransactedApplication.class,  JPATransactedApplication.class);
        // Create the JPA transaction manager, different managers will need different objects in their construct.
        final TransactionManager transactionManager =
                new TransactionManagerJPAImpl(Persistence.createEntityManagerFactory("example-jpa"));
        // Use the special RegistrationModule named TransactionManager,
        // this registers all needed for the container and the service
        // and does a setup for the AspectJTransactionHandler.
        register.register(new TransactionManagerModule(transactionManager, register));
        InjectContainer injectContainer = register.getInjectContainer();
        container = injectContainer;
        return injectContainer;
    }
}