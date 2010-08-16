package com.hrodberaht.inject.extension.transaction.manager.internal;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;

public class TransactionRequired {

    public TransactionRequired() {
    }

    Object transactionHandling(ProceedingJoinPoint thisJoinPoint, TransactionManager transactionManager)
            throws Throwable {
        boolean isTransactionNew = false;
        try {
            if (!transactionManager.isActive()) {
                TransactionLogging.log("TransactionRequired: Begin Transactional call : {0}",
                        thisJoinPoint.getSignature().getName());
                isTransactionNew = transactionManager.begin();
            }
            Object proceed = thisJoinPoint.proceed();
            if (isTransactionNew && transactionManager.isActive()) {
                TransactionLogging.log("TransactionRequired: Commit/Close Transactional call : {0}",
                        thisJoinPoint.getSignature().getName());
                transactionManager.commit();
            }
            return proceed;
        } catch (Throwable error) {
            TransactionLogging.log("TransactionRequired: Error Transactional call : {0}",
                    thisJoinPoint.getSignature().getName());
            if (transactionManager.isActive()) {
                transactionManager.rollback();
            }
            throw error;
        } finally {
            if (isTransactionNew) {
                TransactionLogging.log("TransactionRequired: Close : {0}",
                        thisJoinPoint.getSignature().getName());
                transactionManager.close();
            }
        }
    }


}