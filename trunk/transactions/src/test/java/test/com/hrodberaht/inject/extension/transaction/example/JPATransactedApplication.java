package test.com.hrodberaht.inject.extension.transaction.example;

import com.hrodberaht.inject.extension.transaction.manager.impl.TransactionManagerJPA;
import com.hrodberaht.inject.extension.transaction.manager.impl.TransactionManagerJPAImpl;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collection;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-10 18:29:58
 * @version 1.0
 * @since 1.0
 */
public class JPATransactedApplication implements TransactedApplication{


    @Inject
    private TransactionManagerJPA transactionManager;


    public void createPerson(Person person){
        createPerson(person, false);    
    }

    @TransactionAttribute
    public void createPerson(Person person, boolean fakeException){
        if(fakeException){
            throw new RuntimeException("Bad call, rollbacktime");
        }
        EntityManager em = transactionManager.getEntityManager();
        em.persist(person);
        em.flush();

    }

    @TransactionAttribute
    public void deletePerson(Person person){
        EntityManager em = transactionManager.getEntityManager();
        // Delete must be attached entity, this makes sure that it is (otherwise the entire method must be transactional)
        person = em.find(Person.class, person.getId());
        em.remove(person);
        em.flush();
    }

    @TransactionAttribute
    public void clearLogs() {
        EntityManager em = transactionManager.getEntityManager();
        Query query = em.createQuery("delete from Logging");
        query.executeUpdate();
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Person findPerson(Long id){
        EntityManager em = transactionManager.getEntityManager();
        return em.find(Person.class, id);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public Person findPersonReqNew(Long id){
        EntityManager em = transactionManager.getEntityManager();
        return em.find(Person.class, id);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public void createLog(Logging log){
        EntityManager em = transactionManager.getEntityManager();
        em.persist(log);
        // This is just for the test program, not needed as TransactionManager will perform flush when needed.
        // ITs only here to verify that the transaction manager actually performs creates the TX as intended.
        em.flush();
    }

    public Logging getLog(Long id) {
        EntityManager em = transactionManager.getEntityManager();
        return em.find(Logging.class, id);        
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
        em.flush();
    }

    @TransactionAttribute(value = TransactionAttributeType.MANDATORY)
    public void createPersonMandatory(Person person){        
        EntityManager em = transactionManager.getEntityManager();
        em.persist(person);
        em.flush();
    }

    @TransactionAttribute
    public Person depthyTransactions(Person person){
        // This only works for AspectJ, most AOP frameworks need "lookup" the service again.
        createPerson(person);
        return findPerson(person.getId());        
    }

    @TransactionAttribute
    public Person depthyTransactionsMandatory(Person person){
        // This only works for AspectJ, most AOP frameworks need "lookup" the service again.
        createPersonMandatory(person);
        return findPerson(person.getId());
    }

    @TransactionAttribute
    public Person depthyTransactionsNewTx(Person person){
        // This only works for AspectJ, most AOP frameworks need "lookup" the service again.
        createPersonNewTx(person);
        return findPerson(person.getId());        
    }


    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public Person complexTransactionsNewTx(Person person, Logging log){
        // This only works for AspectJ, most AOP frameworks need "lookup" the service again.
        try{
            createPerson(person, true);
        }catch (Exception e){
            createLog(log);
            // A classic log and ignore, should be shot for using this as demo ...
        }
        return findPerson(person.getId());
    }

    @TransactionAttribute
    public Person depthyTransactionsNotSupported(Person person){
        // This only works for AspectJ, most AOP frameworks need "lookup" the service again.
        createPerson(person);
        return somethingNonTransactional(person.getId());
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Collection<Person> findAllPersons() {
        EntityManager em = transactionManager.getEntityManager();
        TypedQuery<Person> typedQuery = em.createQuery("from Person", Person.class);


        return typedQuery.getResultList();
    }
}
