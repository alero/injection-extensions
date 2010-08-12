package com.hrodberaht.inject.extension.transaction.manager.internal;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;

public class TransactionSupports {

    public TransactionSupports() {
    }

    Object transactionHandling(ProceedingJoinPoint thisJoinPoint, TransactionManager transactionManager) throws Throwable {
        try {
            return thisJoinPoint.proceed();
        } catch (Throwable error) {
            // TODO: hmmm rollback for throwing supports?
            TransactionLogging.transactionLogging("Error Transactional call : {0}", thisJoinPoint.getSignature().getName());
            if (transactionManager.isActive()) {
                transactionManager.rollback();                
            }
            throw error;
        }
    }

    

}