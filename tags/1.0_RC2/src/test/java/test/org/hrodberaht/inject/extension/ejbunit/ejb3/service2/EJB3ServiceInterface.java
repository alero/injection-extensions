package test.org.hrodberaht.inject.extension.ejbunit.ejb3.service2;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:24:39
 * @version 1.0
 * @since 1.0
 */
public interface EJB3ServiceInterface {
    void doSomething();
    String findSomething(Long id);
    void doSomethingDeep();
    String findSomethingDeep(Long id);

}
