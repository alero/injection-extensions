package com.hrodberaht.inject.extension.transaction.manager.internal;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;

public class TransactionMandatory {

    public TransactionMandatory() {
    }

    Object transactionHandling(ProceedingJoinPoint thisJoinPoint, TransactionManager transactionManager) throws Throwable {        
        if (!transactionManager.isActive()) {
            throw new TransactionHandlingError("has no active transaction");
        }
        return thisJoinPoint.proceed();
    }

    

}