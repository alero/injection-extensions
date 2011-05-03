package test.org.hrodberaht.inject.extension.ejbunit.demo.test;

import org.hrodberaht.inject.extension.tdd.ContainerContext;
import org.hrodberaht.inject.extension.tdd.JUnitRunner;
import org.junit.runner.RunWith;
import test.org.hrodberaht.inject.extension.ejbunit.demo.test.config.CourseContainerConfigExample;

/**
 * Inject extension TDD
 *
 * @author Robert Alexandersson
 *         2011-05-03 21:42
 * @created 1.0
 * @since 1.0
 */
@ContainerContext(CourseContainerConfigExample.class)
@RunWith(JUnitRunner.class)
public class TestCustomerAccountServicePersistence {
}
