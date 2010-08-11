package test.com.hrodberaht.inject.extension.transaction.example;

import com.hrodberaht.inject.extension.transaction.manager.JPATransactionManager;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-10 18:29:58
 * @version 1.0
 * @since 1.0
 */
public class JPATransactedApplication {


    @Inject
    private JPATransactionManager transactionManager;



    @TransactionAttribute
    public void createPerson(Person person){
        EntityManager em = transactionManager.getEntityManager();
        em.persist(person);
        // em.flush();
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Person findPerson(Long id){
        EntityManager em = transactionManager.getEntityManager();
        return em.find(Person.class, id);
    }

    @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
    public Person somethingNonTransactional(Long id){
        EntityManager em = transactionManager.getEntityManager();
        return em.find(Person.class, id);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public void createPersonNewTx(Person person){
        EntityManager em = transactionManager.getEntityManager();
        em.persist(person);
    }

    @TransactionAttribute(value = TransactionAttributeType.MANDATORY)
    public void createPersonMandatory(Person person){
        EntityManager em = transactionManager.getEntityManager();
        em.persist(person);
    }

    @TransactionAttribute
    public Person depthyTransactions(Person person){
        createPerson(person);
        return findPerson(person.getId());        
    }

    @TransactionAttribute
    public Person depthyTransactionsMandatory(Person person){
        createPersonMandatory(person);
        return findPerson(person.getId());
    }

    @TransactionAttribute
    public Person depthyTransactionsNewTx(Person person){
        createPersonNewTx(person);
        return findPerson(person.getId());        
    }

    @TransactionAttribute
    public Person depthyTransactionsNotSupported(Person person){
        createPerson(person);
        return somethingNonTransactional(person.getId());
    }

}
