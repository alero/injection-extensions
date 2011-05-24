package test.org.hrodberaht.inject.extension.ejbunit.demo.service;

import test.org.hrodberaht.inject.extension.ejbunit.demo.model.Customer;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Inject extension TDD
 *
 * @author Robert Alexandersson
 *         2011-05-03 20:24
 * @created 1.0
 * @since 1.0
 */
@Stateless
public class CustomerServiceBean implements CustomerService{

    @PersistenceContext(unitName="example-jpa")
    protected EntityManager entityManager;

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public Customer create(Customer customer) {
        entityManager.persist(customer);
        return customer;
    }

    public Customer find(Long id) {
        return entityManager.find(Customer.class, id);
    }
}
