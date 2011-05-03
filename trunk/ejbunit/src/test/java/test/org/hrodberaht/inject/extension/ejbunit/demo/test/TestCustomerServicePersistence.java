package test.org.hrodberaht.inject.extension.ejbunit.demo.test;

import org.hrodberaht.inject.extension.tdd.ContainerContext;
import org.hrodberaht.inject.extension.tdd.JUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.org.hrodberaht.inject.extension.ejbunit.demo.model.Customer;
import test.org.hrodberaht.inject.extension.ejbunit.demo.service.CustomerService;
import test.org.hrodberaht.inject.extension.ejbunit.demo.test.config.CourseContainerConfigExample;

import javax.ejb.EJB;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Inject extension TDD
 *
 * @author Robert Alexandersson
 *         2011-05-03 20:05
 * @created 1.0
 * @since 1.0
 */
@ContainerContext(CourseContainerConfigExample.class)
@RunWith(JUnitRunner.class)
public class TestCustomerServicePersistence {


    @EJB
    private CustomerService customerService;

    /**
     * Test Persistence create/read
     */
    @Test
    public void testCustomerCreateRead(){
        Customer customer = CourseDataModelStub.createCustomer();

        customerService.create(customer);

        Customer foundCustomer = customerService.find(customer.getId());

        assertEquals(customer.getId(), foundCustomer.getId());
        assertEquals(customer.getName(), foundCustomer.getName());

    }

    /**
     * Test Persistence read from pre-created values (via insert script 'insert_script_customer.sql')
     */
    @Test
    public void testCustomerRead(){

        Customer foundCustomer = customerService.find(-1L);

        assertNotNull(foundCustomer);
        assertEquals("The Dude", foundCustomer.getName());

    }


}
