package com.hrodberaht.inject.extension.transaction.manager.impl;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.RequiresNewTransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.internal.TransactionHandlingError;

/**
 * Injection Transaction Extension
 *
 * @author Robert Alexandersson
 *         2010-aug-21 20:29:06
 * @version 1.0
 * @since 1.0
 */
public abstract class TransactionManagerBase<T> implements TransactionManager, RequiresNewTransactionManager {

    protected abstract void closeNative(T nativeTransaction);

    protected abstract T createNativeManager();

    protected abstract TransactionHolder<T> createTransactionHolder(TransactionHolder<T> holder);

    protected abstract TransactionHolder<T> createTransactionHolder();

    protected abstract void postInitHolder(TransactionHolder<T> holder);

    public abstract boolean requiresNewDisabled();

    public abstract void disableRequiresNew();

    public abstract void enableRequiresNew();

    public abstract TransactionScopeHandler getTransactionScopeHandler();


    protected void cleanupTransactionHolder(TransactionHolder<T> holder) {
        holder.setNativeManager(null);
        if (holder.getParentTransaction() == null) {
            getTransactionScopeHandler().set(null);
        } else {
            // cleanup self reference
            holder.getParentTransaction().setChildTransaction(null);
        }
        closeAllChildren(holder);
    }

    private void closeAllChildren(TransactionHolder<T> holder) {
        if (holder.getChildTransaction() != null) {
            closeNative(holder.getChildTransaction().getNativeManager());
            holder.getChildTransaction().setNativeManager(null);
            holder.getChildTransaction().setParentTransaction(null);
            closeAllChildren(holder.getChildTransaction());
        }
    }

    protected TransactionHolder<T> findCreateManagerHolder() {
        TransactionHolder<T> manager = getTransactionScopeHandler().get();
        if (manager == null) {
            manager = createTransactionHolder();
            getTransactionScopeHandler().set(manager);
        } else if (manager.isNew) {
            manager.isNew = false;
        }
        return manager;
    }


    public T getNativeManager() {
        TransactionHolder<T> holder = findDeepestHolder();
        if (holder == null) {
            // This can happend when NOT_SUPPORTED tries to lookup a Manager
            throw new TransactionHandlingError("The EntityManager has not been initialized with a transaction holder");
        } else if (holder.getNativeManager() == null) {
            // Late create of manager (for SUPPORTS)
            holder.setNativeManager(createNativeManager());
            postInitHolder(holder);
        }
        return holder.getNativeManager();
    }

    protected TransactionHolder<T> findAndInitDeepestHolder() {
        TransactionHolder<T> holder = getTransactionScopeHandler().get();
        if (holder == null) {
            // If first transaction is REW_NEW, just do as normal.
            holder = createTransactionHolder();
            getTransactionScopeHandler().set(holder);
            return holder;
        }
        while (holder.getChildTransaction() != null) {
            holder = holder.getChildTransaction();
        }

        holder.setChildTransaction(createTransactionHolder(holder));
        return holder.getChildTransaction();
    }


    protected TransactionHolder<T> findDeepestHolder() {
        TransactionHolder<T> holder = getTransactionScopeHandler().get();
        if (holder == null) {
            return null;
        }
        while (holder.getChildTransaction() != null) {
            holder = holder.getChildTransaction();
        }
        return holder;
    }

}
