package test.org.hrodberaht.inject.extension.ejbunit.demo.service;

import test.org.hrodberaht.inject.extension.ejbunit.demo.model.CustomerAccount;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Inject extension TDD
 *
 * @author Robert Alexandersson
 *         2011-05-03 21:43
 * @created 1.0
 * @since 1.0
 */
@Local
public class CustomerAccountServiceBean implements CustomerAccountService{

    @PersistenceContext(unitName="example-jpa")
    protected EntityManager entityManager;

    public CustomerAccount create(CustomerAccount customer) {
        entityManager.persist(customer);
        return customer;
    }

    public CustomerAccount find(Long id) {
        return entityManager.find(CustomerAccount.class, id);
    }
}
