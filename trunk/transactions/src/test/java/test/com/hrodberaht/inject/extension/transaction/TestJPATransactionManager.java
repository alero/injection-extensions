package test.com.hrodberaht.inject.extension.transaction;

import org.hrodberaht.inject.Container;
import org.hrodberaht.inject.InjectionRegisterJava;
import org.junit.Test;
import test.com.hrodberaht.inject.extension.transaction.example.JPATransactedApplication;
import test.com.hrodberaht.inject.extension.transaction.example.Person;

import javax.ejb.TransactionAttribute;

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
    public void testCreateManager(){
        
        // new InjectionContainerSPI().changeInstanceCreator(new AspectJTransactionHandler());

        InjectionRegisterJava register = new InjectionRegisterJava();
        register.register(JPATransactedApplication.class);

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
