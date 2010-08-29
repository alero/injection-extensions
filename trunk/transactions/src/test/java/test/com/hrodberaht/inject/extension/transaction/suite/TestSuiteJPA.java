package test.com.hrodberaht.inject.extension.transaction.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import test.com.hrodberaht.inject.extension.transaction.TestJPANewTransactionScope;
import test.com.hrodberaht.inject.extension.transaction.TestJPANewTransactionScopeDisabled;
import test.com.hrodberaht.inject.extension.transaction.TestJPATransactionManager;
import test.com.hrodberaht.inject.extension.transaction.TestSimpleServiceWithRunner;

/**
 * Injection Transaction Extension
 *
 * @author Robert Alexandersson
 *         2010-aug-22 18:18:03
 * @version 1.0
 * @since 1.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestJPATransactionManager.class
        , TestJPANewTransactionScope.class
        , TestJPANewTransactionScopeDisabled.class
        , TestSimpleServiceWithRunner.class
        // TestJPATransactionManagerPerformance.class
})
public class TestSuiteJPA {
}
