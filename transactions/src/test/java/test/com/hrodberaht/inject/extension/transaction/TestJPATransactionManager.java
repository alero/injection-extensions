package test.com.hrodberaht.inject.extension.transaction;

import com.hrodberaht.inject.extension.transaction.manager.JPATransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.RegistrationTransactionManager;
import org.hrodberaht.inject.Container;
import org.hrodberaht.inject.InjectionRegisterModule;
import org.junit.Test;
import test.com.hrodberaht.inject.extension.transaction.example.JPATransactedApplication;
import test.com.hrodberaht.inject.extension.transaction.example.Person;

import javax.ejb.TransactionAttribute;
import javax.persistence.Persistence;

import static org.junit.Assert.assertEquals;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-06 18:33:12
 * @version 1.0
 * @since 1.0
 */
@TransactionAttribute
public class TestJPATransactionManager {


    @Test
    public void testCreateManager() {

        // new InjectionContainerSPI().changeInstanceCreator(new AspectJTransactionHandler());

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

        Container container = register.getContainer();

        JPATransactedApplication application = container.get(JPATransactedApplication.class);

        Person person = new Person();
        person.setId(1L);
        person.setName("Dude");
        application.addPerson(person);

        Person foundPerson = application.findPerson(1L);
        assertEquals(foundPerson.getName(), person.getName());

    }

}
