package com.hrodberaht.inject.extension.transaction.manager.impl;


/**
 * Injection Transaction Extension
 *
 * @author Robert Alexandersson
 *         2010-aug-21 20:29:06
 * @version 1.0
 * @since 1.0
 */
public class TransactionHolder<T> {

    public boolean isNew = true;
    protected T nativeManager = null;
    protected boolean disableRequiresNew = false;

    protected TransactionHolder<T> childTransaction = null;
    protected TransactionHolder<T> parentTransaction = null;

    public TransactionHolder() {
    }

    public boolean isNew() {
        return isNew;
    }

    public T getNativeManager() {
        return nativeManager;
    }

    public void setNativeManager(T nativeManager) {
        this.nativeManager = nativeManager;
    }

    public boolean isDisableRequiresNew() {
        return disableRequiresNew;
    }

    public TransactionHolder<T> getChildTransaction() {
        return childTransaction;
    }

    public TransactionHolder<T> getParentTransaction() {
        return parentTransaction;
    }

    public void setParentTransaction(TransactionHolder<T> parentTransaction) {
        this.parentTransaction = parentTransaction;
    }

    public void setChildTransaction(TransactionHolder<T> childTransaction) {
        this.childTransaction = childTransaction;
    }
}