package test.com.hrodberaht.inject.extension.transaction.example;

import com.hrodberaht.inject.extension.transaction.manager.AspectJTransactionHandler;
import com.hrodberaht.inject.extension.transaction.manager.JPATransactionManager;

import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-10 18:29:58
 * @version 1.0
 * @since 1.0
 */
public class JPATransactedApplication {

    private static JPATransactionManager transactionManager = null;
    static{
        transactionManager =
                new JPATransactionManager(Persistence.createEntityManagerFactory("example-jpa"));
        AspectJTransactionHandler.setTransactionManager(transactionManager);
    }



    @TransactionAttribute
    public void addPerson(Person person){
        EntityManager em = transactionManager.getEntityManager();
        em.persist(person);
        // em.flush();
    }

    @TransactionAttribute
    public Person findPerson(Long id){
        EntityManager em = transactionManager.getEntityManager();
        return em.find(Person.class, id);
    }

}
