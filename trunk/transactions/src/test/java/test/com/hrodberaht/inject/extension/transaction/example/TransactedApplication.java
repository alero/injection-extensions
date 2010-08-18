package test.com.hrodberaht.inject.extension.transaction.example;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Robert Work
 * Date: 2010-aug-15
 * Time: 19:49:35
 * To change this template use File | Settings | File Templates.
 */
public interface TransactedApplication {

    Person findPerson(Long id);
    Person findPersonReqNew(Long id);
    Collection<Person> findAllPersons();
    Person somethingNonTransactional(Long id);
    void createPerson(Person person);
    void createPersonNewTx(Person person);
    void createPersonMandatory(Person person);

    // Mixes create and find
    Person depthyTransactions(Person person);
    Person depthyTransactionsMandatory(Person person);
    Person depthyTransactionsNewTx(Person person);
    Person depthyTransactionsNotSupported(Person person);

    void fakeOperationForPerformanceTest();

    Person complexTransactionsNewTx(Person person, Logging log);

    void createLog(Logging log);
    Logging getLog(Long id);

    void deletePerson(Person person);

    void clearLogs();
}
