package test.com.hrodberaht.inject.extension.transaction;

import com.hrodberaht.inject.extension.transaction.junit.InjectionContainerContext;
import com.hrodberaht.inject.extension.transaction.junit.InjectionJUnitTestRunner;
import com.hrodberaht.inject.extension.transaction.junit.TransactionDisabled;
import com.hrodberaht.inject.extension.transaction.manager.internal.TransactionHandlingError;
import com.hrodberaht.inject.extension.transaction.manager.internal.TransactionLogging;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.com.hrodberaht.inject.extension.transaction.example.ModuleContainerForTests;
import test.com.hrodberaht.inject.extension.transaction.example.Person;
import test.com.hrodberaht.inject.extension.transaction.example.TransactedApplication;

import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * Injection Transaction Extension
 *
 * @author Robert Alexandersson
 *         2010-aug-06 18:33:12
 * @version 1.0
 * @since 1.0
 *        <p/>
 *        To run these tests with load time weaving add the weaver to the JRE like this.
 *        -javaagent:C:/Users/Robert/.m2/repository/org/aspectj/aspectjweaver/1.6.9/aspectjweaver-1.6.9.jar
 *        If the path contains a space do it like this
 *        -javaagent:"C:\Users\Robert Work\.m2\repository\org\aspectj\aspectjweaver\1.6.9\aspectjweaver-1.6.9.jar"
 */
@InjectionContainerContext(ModuleContainerForTests.class)
@RunWith(InjectionJUnitTestRunner.class)
@TransactionAttribute
public class TestJPATransactionManager {


    @Inject
    private TransactedApplication application;


    @BeforeClass
    public static void init() {
        TransactionLogging.enableLogging = true;
    }

    @AfterClass
    public static void destroy() {
        TransactionLogging.enableLogging = false;

    }

    @Test
    public void testCreateManager() {

        Person person = StubUtil.createPerson();
        application.createPerson(person);

        Person foundPerson = application.findPerson(person.getId());
        assertEquals(foundPerson.getName(), person.getName());

    }


    @Test
    public void testCreateManagerInOneTransaction() {

        Person person = StubUtil.createPerson();
        Person foundPerson = application.depthyTransactions(person);

        assertEquals(foundPerson.getName(), person.getName());

    }

    @Test(expected = TransactionHandlingError.class)
    @TransactionDisabled
    public void testCreateManagerMandatoryFail() {
        System.out.println(Thread.currentThread().getContextClassLoader().getClass().getName());
        Person person = StubUtil.createPerson();
        application.createPersonMandatory(person);


    }

    @Test
    public void testCreateManagerInOneTransactionMandatory() {


        Person person = StubUtil.createPerson();
        Person foundPerson = application.depthyTransactionsMandatory(person);

        assertEquals(foundPerson.getName(), person.getName());

    }

    @Test
    public void testCreateManagerInOneTransactionRequiresNew() {

        Person person = StubUtil.createPerson();
        Person foundPerson = application.depthyTransactionsNewTx(person);

        assertEquals(foundPerson.getName(), person.getName());

    }

    @Test(expected = TransactionHandlingError.class)
    public void testCreateManagerInOneTransactionNotSupported() {

        Person person = StubUtil.createPerson();
        application.depthyTransactionsNotSupported(person);

    }

    @Test(expected = TransactionHandlingError.class)
    @TransactionDisabled
    public void testCreateManagerNotSupported() {

        Person person = new Person();
        person.setId(55L);
        person.setName("Dude");
        application.createPerson(person);

        Person foundPerson = application.somethingNonTransactional(person.getId());

        assertEquals(foundPerson.getName(), person.getName());

    }


}
