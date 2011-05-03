package test.org.hrodberaht.inject.extension.ejbunit.demo.test;

import org.hrodberaht.inject.extension.tdd.ContainerContext;
import org.hrodberaht.inject.extension.tdd.ContainerLifeCycleTestUtil;
import org.hrodberaht.inject.extension.tdd.JUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
public class TestAccountServiceMocking {

    @EJB
    private CustomerAccountService customerAccountService;

    @EJB
    private CustomerService customerService;

    @Test
    public void testAccountAddMoneyMockedUpdate() throws Exception {

        // Prepare data (no mocking active)
        Customer customer = customerService.find(-1L);
        CustomerAccount customerAccount = CourseDataModelStub.createCustomerAccountEmpty(customer);
        customerAccountService.create(customerAccount);

        // Register mocking
        CustomerAccountService customerAccountMock = Mockito.mock(CustomerAccountService.class);
        // Register prepared data as return
        Mockito.when(customerAccountMock.find(Mockito.anyLong())).thenReturn(customerAccount);
        ContainerLifeCycleTestUtil.registerServiceInstance(CustomerAccountService.class, customerAccountMock);

        // Re "fetch" the service intended for testing, all registration changes will be valid
        AccountingService accountingService = ContainerLifeCycleTestUtil.getService(AccountingService.class);
        accountingService.addMoney(500D, customerAccount.getId());

        Mockito.verify(customerAccountMock).find(customerAccount.getId());
        Mockito.verify(customerAccountMock).update(Mockito.<CustomerAccount>any());

    }

    @Test
    public void testAccountAddMoneyMockedAdvancedUpdate() throws Exception {

        // Prepare data (no mocking active)
        Customer customer = customerService.find(-1L);
        final CustomerAccount customerAccount = CourseDataModelStub.createCustomerAccountEmpty(customer);
        customerAccountService.create(customerAccount);

        // Register mocking
        final CustomerAccount[] customerAccountReturn = new CustomerAccount[1];
        CustomerAccountService customerAccountMock = new CustomerAccountService(){
            public CustomerAccount create(CustomerAccount customer) {
                return customer;
            }
            public CustomerAccount find(Long id) {
                return customerAccount;
            }
            public CustomerAccount update(CustomerAccount customerAccount) {
                customerAccountReturn[0] = customerAccount;
                return customerAccount;
            }
        };
        // Register the Service
        ContainerLifeCycleTestUtil.registerServiceInstance(CustomerAccountService.class, customerAccountMock);

        // Re "fetch" the service intended for testing, all registration changes will be valid
        AccountingService accountingService = ContainerLifeCycleTestUtil.getService(AccountingService.class);
        accountingService.addMoney(500D, customerAccount.getId());

        assertEquals(new Double(500D), customerAccountReturn[0].getMoney());

    }
}
