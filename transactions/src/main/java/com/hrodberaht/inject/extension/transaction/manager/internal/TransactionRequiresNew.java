package com.hrodberaht.inject.extension.transaction.manager.internal;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;

public class TransactionRequiresNew {

    public TransactionRequiresNew() {
    }

    Object transactionHandling(ProceedingJoinPoint thisJoinPoint, TransactionManager transactionManager) throws Throwable {
        try {
            // TODO: this needs to be rewritten totally to somehow support depth withing transaction managers
            TransactionLogging.transactionLogging("Begin Transactional call : {0}", thisJoinPoint.getSignature().getName());
            transactionManager.begin();

            Object proceed = thisJoinPoint.proceed();

            TransactionLogging.transactionLogging("Commit/Close Transactional call : {0}", thisJoinPoint.getSignature().getName());
            transactionManager.commit();
            transactionManager.close();

            return proceed;
        } catch (Throwable error) {
            TransactionLogging.transactionLogging("Error Transactional call : {0}", thisJoinPoint.getSignature().getName());
            if (transactionManager.isActive()) {
                transactionManager.rollback();                
            }
            throw error;
        }
    }

    

}