package test.org.hrodberaht.inject.extension.ejbunit.demo.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Inject extension TDD
 *
 * @author Robert Alexandersson
 *         2011-05-03 20:07
 * @created 1.0
 * @since 1.0
 */
@Stateless
public class AccountingServiceBean implements AccountingService{


    @PersistenceContext(unitName="example-jpa")
    protected EntityManager entityManager;

}
