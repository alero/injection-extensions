package test.org.hrodberaht.inject.extension.ejbunit.demo.test;

import org.hrodberaht.inject.extension.tdd.ContainerContext;
import org.hrodberaht.inject.extension.tdd.JUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.org.hrodberaht.inject.extension.ejbunit.demo.model.Customer;
import test.org.hrodberaht.inject.extension.ejbunit.demo.model.CustomerAccount;
import test.org.hrodberaht.inject.extension.ejbunit.demo.service.AccountingService;
import test.org.hrodberaht.inject.extension.ejbunit.demo.service.CustomerAccountService;
import test.org.hrodberaht.inject.extension.ejbunit.demo.service.CustomerService;
import test.org.hrodberaht.inject.extension.ejbunit.demo.test.config.CourseContainerConfigExample;

import javax.ejb.EJB;

import static org.junit.Assert.assertEquals;

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
public class TestAccountService {

    @EJB
    private AccountingService accountingService;

    @EJB
    private CustomerAccountService customerAccountService;

    @EJB
    private CustomerService customerService;


    @Test
    public void testAccountAddMoney() throws Exception {
        Customer customer = customerService.find(-1L);
        CustomerAccount customerAccount = CourseDataModelStub.createCustomerAccountEmpty(customer);

        customerAccountService.create(customerAccount);

        accountingService.addMoney(500D, customerAccount.getId());

        CustomerAccount foundCustomerAccount = customerAccountService.find(customerAccount.getId());

        assertEquals(new Double(500D), foundCustomerAccount.getMoney());

    }

}
