package test.org.hrodberaht.inject.extension.ejbunit.ejb2;

import org.hrodberaht.inject.extension.ejbunit.EJBContainerContext;
import org.hrodberaht.inject.extension.ejbunit.EJBJUnitRunner;
import org.junit.runner.RunWith;
import test.org.hrodberaht.inject.extension.ejbunit.ejb3.config.EJBContainerConfigExample;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 20:39:39
 * @version 1.0
 * @since 1.0
 */
@EJBContainerContext(EJBContainerConfigExample.class)
@RunWith(EJBJUnitRunner.class)
public class TestEJB2ServiceContext {

    

}
