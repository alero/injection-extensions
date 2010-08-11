package com.hrodberaht.inject.extension.transaction.manager;

import com.hrodberaht.inject.extension.transaction.TransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-jul-26 22:58:42
 * @version 1.0
 * @since 1.0
 */
public class JPATransactionManager implements TransactionManager {

    private EntityManagerFactory emf = null;
    private static final InheritableThreadLocal<EntityManagerHolder> entityManagerScopeThreadLocal =
            new InheritableThreadLocal<EntityManagerHolder>();

    private EntityManager findCreateManager(){
        EntityManagerHolder manager = entityManagerScopeThreadLocal.get();
        if(manager == null){
            manager = new EntityManagerHolder(emf.createEntityManager());
            entityManagerScopeThreadLocal.set(manager);
        }
        return manager.entityManager;
    }

    public JPATransactionManager(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void begin() {
        EntityManager em = findCreateManager();
        em.getTransaction().begin();
    }

    public void commit() {
        EntityManager em = findCreateManager();
        em.getTransaction().commit();
    }

    public void rollback() {
        EntityManager em = findCreateManager();
        em.getTransaction().rollback();
    }

    public boolean isActive() {
        EntityManager em = findCreateManager();
        return em.getTransaction().isActive();
    }

    public boolean isLastActive() {
        return entityManagerScopeThreadLocal.get().transactionDepth == 0;
    }

    public void close() {
        EntityManager em = findCreateManager();
        // em.flush();
        // em.clear();
        em.close();
        entityManagerScopeThreadLocal.set(null);
    }

    public void addTransactionDepth() {
        entityManagerScopeThreadLocal.get().addTransactionDepth();
    }

    public void removeTransactionDepth() {
        EntityManagerHolder managerHolder = entityManagerScopeThreadLocal.get();
        if(managerHolder != null){
            managerHolder.removeTransactionDepth();
        }
    }

    public EntityManager getEntityManager() {
        return findCreateManager();
    }

    private class EntityManagerHolder {
        private EntityManager entityManager;
        private int transactionDepth = 0;

        public EntityManagerHolder(EntityManager entityManager) {
            this.entityManager = entityManager;   
        }

        public void removeTransactionDepth(){
            transactionDepth--;
        }

        public void addTransactionDepth(){
            transactionDepth++;   
        }
    }
}
