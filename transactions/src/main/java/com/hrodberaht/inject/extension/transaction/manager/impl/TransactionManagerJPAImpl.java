package com.hrodberaht.inject.extension.transaction.manager.impl;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.RequiresNewTransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.internal.TransactionLogging;

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
public class TransactionManagerJPAImpl implements TransactionManagerJPA, TransactionManager, RequiresNewTransactionManager {

    private EntityManagerFactory emf = null;
    private static final InheritableThreadLocal<EntityManagerHolder> entityManagerScope =
            new InheritableThreadLocal<EntityManagerHolder>();

    private EntityManagerHolder findCreateManagerHolder(){
        EntityManagerHolder manager = entityManagerScope.get();
        if(manager == null){
            manager = new EntityManagerHolder(createEntityManager());
            entityManagerScope.set(manager);
        }else if(manager.isNew){
            manager.isNew = false;    
        }
        return manager;
    }

    private EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    public EntityManager getEntityManager() {
        EntityManagerHolder holder = findDeepestHolder();
        if(holder == null){
            throw new IllegalAccessError("The EntityManager has not been initialized with a transaction ");
        }else if(holder.entityManager == null){
            // Late create of manager (SUPPORTS)
            holder.entityManager = createEntityManager();
        }
        return holder.entityManager;
    }
    
    public TransactionManagerJPAImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public boolean begin() {
        EntityManagerHolder emh = findCreateManagerHolder();
        if(emh.entityManager.getTransaction().isActive()){
            throw new IllegalStateException("Transaction already active");        
        }
        emh.entityManager.getTransaction().begin();
        TransactionLogging.log("TransactionManagerJPAImpl: Tx Begin for session {0}", emh.entityManager);
        return emh.isNew;
    }

    public void commit() {
        EntityManager em = findCreateManagerHolder().entityManager;
        em.getTransaction().commit();
        TransactionLogging.log("TransactionManagerJPAImpl: Tx Commit for session {0}", em);
    }

    public void rollback() {
        EntityManager em = findCreateManagerHolder().entityManager;
        rollbackEntityManager(em);
        TransactionLogging.log("TransactionManagerJPAImpl: Tx Rollback for session {0}", em);
    }

    private void rollbackEntityManager(EntityManager em) {
        em.getTransaction().rollback();
        em.clear();
    }

    public boolean isActive() {
        EntityManagerHolder emh = entityManagerScope.get();
        if(emh == null){
            return false;
        }
        EntityManager em = emh.entityManager;
        return em.getTransaction().isActive();
    }

    public void close() {
        if(!isClosed()){
            EntityManagerHolder emh = entityManagerScope.get();
            TransactionLogging.log("TransactionManagerJPAImpl: Tx Close for session {0}", emh.entityManager);
            closeEntityManager(emh.entityManager);
            emh.entityManager = null;
            entityManagerScope.set(null);
        }
    }

    private void closeEntityManager(EntityManager em) {
        //em.flush();
        //em.clear();
        em.close();
    }

    public boolean initTransactionHolder() {
        EntityManagerHolder managerHolder = entityManagerScope.get();
        if(managerHolder == null){
            managerHolder = new EntityManagerHolder();
            entityManagerScope.set(managerHolder);
            return true;
        }
        return false;
    }

    private boolean isClosed() {
        EntityManagerHolder managerHolder = entityManagerScope.get();
        if(managerHolder != null){
            return false;
        }
        return true;
    }



    public void newBegin() {
        EntityManagerHolder holder = findAndInitDeepestHolder();
        holder.entityManager.getTransaction().begin();
        TransactionLogging.log("TransactionManagerJPAImpl: NewTx Begin for session {0}", holder.entityManager);

    }

    public void newCommit() {
        EntityManagerHolder holder = findDeepestHolder();
        holder.entityManager.getTransaction().commit();
        // cleanupTransactionHolder(holder);
        TransactionLogging.log("TransactionManagerJPAImpl: NewTx Commit for session {0}", holder.entityManager);
    }

    public void newRollback() {
        EntityManagerHolder holder = findDeepestHolder();
        rollbackEntityManager(holder.entityManager);
        // cleanupTransactionHolder(holder);
        TransactionLogging.log("TransactionManagerJPAImpl: NewTx Rollback for session {0}", holder.entityManager);
    }

    public void newClose() {
        EntityManagerHolder holder = findDeepestHolder();
        TransactionLogging.log("TransactionManagerJPAImpl: NewTX closing session {0}", holder.entityManager);
        closeEntityManager(holder.entityManager);

        cleanupTransactionHolder(holder);
    }

    private void cleanupTransactionHolder(EntityManagerHolder holder) {        
        holder.entityManager = null;
        if(holder.parentTransaction == null){
            entityManagerScope.set(null);
        }
        closeAllChildren(holder);
    }

    private void closeAllChildren(EntityManagerHolder holder) {
        if(holder.childTransaction != null){
            holder.childTransaction.entityManager.close();
            holder.childTransaction.entityManager = null;
            holder.childTransaction.parentTransaction = null;
            closeAllChildren(holder.childTransaction);
        }
    }

    public boolean newIsActive() {
        EntityManagerHolder holder = findDeepestHolder();
        return holder.entityManager.getTransaction().isActive();        
    }


    private EntityManagerHolder findAndInitDeepestHolder() {
        EntityManagerHolder holder = entityManagerScope.get();
        if(holder == null){
            // If first transaction is REW_NEW, just do as normal.
            holder = new EntityManagerHolder(createEntityManager());
            entityManagerScope.set(holder);
            return holder;
        }
        while(holder.childTransaction != null){
            holder = holder.childTransaction;
        }
        
        holder.childTransaction = new EntityManagerHolder(createEntityManager(), holder);
        return holder.childTransaction;
    }

    private EntityManagerHolder findDeepestHolder() {
        EntityManagerHolder holder = entityManagerScope.get();
        if(holder == null){
            throw new IllegalStateException("No transaction support active");    
        }
        while(holder.childTransaction != null){
            holder = holder.childTransaction;
        }
        return holder;
    }

    private class EntityManagerHolder {
        private boolean isNew = true;
        private EntityManager entityManager = null;
        private int transactionDepth = 0;

        private EntityManagerHolder childTransaction = null;
        private EntityManagerHolder parentTransaction = null;

        public EntityManagerHolder() {
        }

        public EntityManagerHolder(EntityManager entityManager) {
            this.entityManager = entityManager;   
        }

        public EntityManagerHolder(EntityManager entityManager, EntityManagerHolder holder) {
            this.entityManager = entityManager;
            this.parentTransaction = holder;
        }

        public void removeTransactionDepth(){
            transactionDepth--;
        }

        public void addTransactionDepth(){
            transactionDepth++;   
        }
    }
}
