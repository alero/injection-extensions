package test.com.hrodberaht.inject.extension.transaction;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.junit.InjectionContainerContext;
import com.hrodberaht.inject.extension.transaction.junit.InjectionJUnitTestRunner;
import com.hrodberaht.inject.extension.transaction.manager.internal.TransactionLogging;
import org.hrodberaht.inject.Container;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.com.hrodberaht.inject.extension.transaction.example.Logging;
import test.com.hrodberaht.inject.extension.transaction.example.ModuleContainerForTests;
import test.com.hrodberaht.inject.extension.transaction.example.Person;
import test.com.hrodberaht.inject.extension.transaction.example.TransactedApplication;

import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Injection Transaction Extension
 *
 * @author Robert Alexandersson
 *         2010-aug-11 22:58:13
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        To run these tests with load time weaving add the weaver to the JRE like this.
 *        -javaagent:C:/Users/Robert/.m2/repository/org/aspectj/aspectjweaver/1.6.9/aspectjweaver-1.6.9.jar
 *        If the path contains a space do it like this
 *        -javaagent:"C:\Users\Robert Work\.m2\repository\org\aspectj\aspectjweaver\1.6.9\aspectjweaver-1.6.9.jar"
 */

@InjectionContainerContext(value = ModuleContainerForTests.class, disableRequiresNewTransaction = true)
@RunWith(InjectionJUnitTestRunner.class)
@TransactionAttribute
public class TestJPANewTransactionScopeDisabled {

    @Inject
    private TransactedApplication application;

    @Inject
    private TransactionManager transactionManager;

    @BeforeClass
    public static void initClass() {
        TransactionLogging.enableLogging = true;
    }

    @AfterClass
    public static void destroy() {
        TransactionLogging.enableLogging = false;

        Container container = ModuleContainerForTests.container;
        TransactedApplication application = container.get(TransactedApplication.class);
        Collection<Person> persons = application.findAllPersons();

        assertTrue(persons.size() == 0);

    }


    @Test
    public void testSingleTransactionWithOpenMainTx2() {

        Person person = StubUtil.createPerson();
        application.createPersonNewTx(person);
        // Rollback the transaction, will not rollback the req new (as it should be committed)
        transactionManager.rollback();


        Person foundPerson = application.findPerson(person.getId());
        assertNull(foundPerson);


        assertFalse(transactionManager.isActive());

    }

    @Test
    public void testSingleTransaction2() {

        Person person = StubUtil.createPerson();
        Person foundPerson = application.depthyTransactionsNewTx(person);

        assertEquals(foundPerson.getName(), person.getName());

        assertTrue(transactionManager.isActive());

    }

    @Test
    public void testSingleTransactionWithErrorAndLogging2() {

        Person person = StubUtil.createPerson();
        Logging log = StubUtil.createLogg("A log message");
        Person foundPerson = application.complexTransactionsNewTx(person, log);

        Logging storedLog = application.getLog(log.getId());

        // Everything should be rolled-back and the person should not be saved
        // the log should be there, is its handled in NEW_TX
        assertNull(foundPerson);

        assertEquals(storedLog.getMessage(), log.getMessage());

        assertTrue(transactionManager.isActive());

    }

}
