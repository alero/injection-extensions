package test.org.hrodberaht.inject.extension.ejbunit.ejb3.service;

import javax.ejb.Local;

/**
 * ¤Projectname¤
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:28:55
 * @version 1.0
 * @since 1.0
 */
@Local
public interface EJB3InnerServiceInterface {
    void doSomething();

    String findSomething(Long id);

    String findSomethingFromDataSource(Long id);
}
