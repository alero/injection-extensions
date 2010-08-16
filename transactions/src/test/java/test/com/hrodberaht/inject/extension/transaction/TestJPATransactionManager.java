package test.com.hrodberaht.inject.extension.transaction;

import com.hrodberaht.inject.extension.transaction.junit.InjectionContainerContext;
import com.hrodberaht.inject.extension.transaction.junit.InjectionJUnitTestRunner;
import com.hrodberaht.inject.extension.transaction.junit.TransactionDisabled;
import com.hrodberaht.inject.extension.transaction.manager.internal.TransactionHandlingError;
import com.hrodberaht.inject.extension.transaction.manager.internal.TransactionLogging;
import org.hrodberaht.inject.Container;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.com.hrodberaht.inject.extension.transaction.example.JPATransactedApplication;
import test.com.hrodberaht.inject.extension.transaction.example.ModuleContainerForTests;
import test.com.hrodberaht.inject.extension.transaction.example.Person;
import test.com.hrodberaht.inject.extension.transaction.example.TransactedApplication;

import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-06 18:33:12
 * @version 1.0
 * @since 1.0
 */
@InjectionContainerContext(ModuleContainerForTests.class)
@RunWith(InjectionJUnitTestRunner.class)
@TransactionAttribute
public class TestJPATransactionManager {

    /**
     * To run these tests with load time weaving add the weaver to the JRE like this.
     * -javaagent:C:/Users/Robert/.m2/repository/org/aspectj/aspectjweaver/1.6.9/aspectjweaver-1.6.9.jar
     * If the path contains a space do it like this
     * -javaagent:"C:\Users\Robert Work\.m2\repository\org\aspectj\aspectjweaver\1.6.9\aspectjweaver-1.6.9.jar"
     */




    @Inject
    private TransactedApplication application;



    @BeforeClass
    public static void init() {
        TransactionLogging.enableLogging = true;
    }

    @AfterClass
    public static void destroy(){
        Container container = new ModuleContainerForTests().createContainer();
        JPATransactedApplication application = container.get(JPATransactedApplication.class);
        Collection<Person> collection = application.findAllPersons();

        // Verify that all values are cleared automatically from the test being transactional and calling rollback.
        assertEquals(0, collection.size());

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

    @Test
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
