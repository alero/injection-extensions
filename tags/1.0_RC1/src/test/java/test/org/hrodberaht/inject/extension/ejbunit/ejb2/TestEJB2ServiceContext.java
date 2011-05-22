package test.org.hrodberaht.inject.extension.ejbunit.ejb2;

import org.hrodberaht.inject.extension.tdd.ContainerContext;
import org.hrodberaht.inject.extension.tdd.JUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.org.hrodberaht.inject.extension.ejbunit.ejb2.service.EJB2ServiceBean;
import test.org.hrodberaht.inject.extension.ejbunit.ejb2.config.EJBContainerConfigExample;

import javax.ejb.SessionContext;
import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 20:39:39
 * @version 1.0
 * @since 1.0
 */
@ContainerContext(EJBContainerConfigExample.class)
@RunWith(JUnitRunner.class)
public class TestEJB2ServiceContext {

    @Inject
    private EJB2ServiceBean serviceBean;


    @Test
    public void testServiceBean() {
        String something = serviceBean.getSomething(12L);
        assertEquals("something 12", something);
        SessionContext sessionContext = serviceBean.getSessionContext();
        assertNotNull(sessionContext);

        assertEquals("JUnitUser", sessionContext.getCallerPrincipal().getName());

        // assertEquals(1, sessionContext.getUserTransaction().getStatus());

    }

}
