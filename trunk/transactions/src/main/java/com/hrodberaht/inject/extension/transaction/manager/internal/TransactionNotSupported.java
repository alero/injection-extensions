package com.hrodberaht.inject.extension.transaction.manager.internal;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;

public class TransactionNotSupported {

    public TransactionNotSupported() {
    }

    Object transactionHandling(ProceedingJoinPoint thisJoinPoint, TransactionManager transactionManager) throws Throwable {

        if (transactionManager.isActive()) {            
            throw new TransactionHandlingError("has active transaction");
        }
        // So a TransactionHolder (not a transaction) can be used
        transactionManager.initTransactionHolder();
        return thisJoinPoint.proceed();

    }


}