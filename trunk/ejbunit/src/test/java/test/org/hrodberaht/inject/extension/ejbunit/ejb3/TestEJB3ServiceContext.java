package test.org.hrodberaht.inject.extension.ejbunit.ejb3;

import org.hrodberaht.inject.extension.tdd.ContainerContext;
import org.hrodberaht.inject.extension.tdd.JUnitRunner;
import org.hrodberaht.inject.extension.tdd.ResourceHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import test.org.hrodberaht.inject.extension.ejbunit.ejb3.config.EJBContainerConfigExample;
import test.org.hrodberaht.inject.extension.ejbunit.ejb3.config.MockedInnerModule;
import test.org.hrodberaht.inject.extension.ejbunit.ejb3.service.EJB3InnerServiceInterface;
import test.org.hrodberaht.inject.extension.ejbunit.ejb3.service.EJB3ServiceInterface;

import javax.ejb.EJB;
import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:31:30
 * @version 1.0
 * @since 1.0
 */
@ContainerContext(EJBContainerConfigExample.class)
@RunWith(JUnitRunner.class)
public class TestEJB3ServiceContext {


    @Inject
    private EJB3ServiceInterface ejb3ServiceInterfaceWithInject;

    @EJB
    private EJB3ServiceInterface anInterface;

    @Test
    public void testEJBWiring(){
        String something = anInterface.findSomething(12L);
        assertEquals("Something", something);

        String somethingDeep = anInterface.findSomethingDeep(12L);
        assertEquals("Something Deep", somethingDeep);
    }

    @Test
    public void testEJBInjectWiring(){
        String something = ejb3ServiceInterfaceWithInject.findSomething(12L);
        assertEquals("Something", something);

        String somethingDeep = ejb3ServiceInterfaceWithInject.findSomethingDeep(12L);
        assertEquals("Something Deep", somethingDeep);
    }

    @Test
    public void testEJBResourceInjectionAndUpdate(){
        String something = anInterface.findSomethingDeepWithDataSource(12L);
        assertEquals("The Name", something);
        anInterface.updateSomethingInDataSource(12L, "A new Name");
        something = anInterface.findSomethingDeepWithDataSource(12L);
        assertEquals("A new Name", something);
    }

    @Test
    public void testEJBResourceInjection(){
        String something = ejb3ServiceInterfaceWithInject.findSomethingDeepWithDataSource(12L);
        assertEquals("The Name", something);
        
    }



    @Test
    public void testEJBWiringWithMockito(){

        EJB3InnerServiceInterface anInterface = Mockito.mock(EJB3InnerServiceInterface.class);
        Mockito.when(anInterface.findSomething(12L)).thenReturn("Something Deep From Mock");
        ResourceHandler.registerServiceInstance(EJB3InnerServiceInterface.class, anInterface);

        EJB3ServiceInterface serviceInterface = ResourceHandler.getService(EJB3ServiceInterface.class);
        String something = serviceInterface.findSomething(12L);
        assertEquals("Something", something);

        String somethingDeep = serviceInterface.findSomethingDeep(12L);
        assertEquals("Something Deep From Mock", somethingDeep);
    }

    @Test
    public void testModuleRegistration(){
        ResourceHandler.registerModule(new MockedInnerModule());

        EJB3ServiceInterface serviceInterface = ResourceHandler.getService(EJB3ServiceInterface.class);
        String something = serviceInterface.findSomethingDeep(12L);
        assertEquals("Mocked", something);

    }

}
