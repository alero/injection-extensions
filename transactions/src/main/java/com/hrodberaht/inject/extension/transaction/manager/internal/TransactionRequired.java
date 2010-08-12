package com.hrodberaht.inject.extension.transaction.manager.internal;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;

public class TransactionRequired {

    public TransactionRequired() {
    }

    Object transactionHandling(ProceedingJoinPoint thisJoinPoint, TransactionManager transactionManager) throws Throwable {
        try {
            if (!transactionManager.isActive()) {
                TransactionLogging.transactionLogging("Begin Transactional call : {0}", thisJoinPoint.getSignature().getName());
                transactionManager.begin();
            } else {
                TransactionLogging.transactionLogging("Added depth for Transactional call : {0}", thisJoinPoint.getSignature().getName());
                transactionManager.addTransactionDepth();
            }
            Object proceed = thisJoinPoint.proceed();
            if (transactionManager.isLastActive() && transactionManager.isActive()) {
                TransactionLogging.transactionLogging("Commit/Close Transactional call : {0}", thisJoinPoint.getSignature().getName());
                transactionManager.commit();
                transactionManager.close();
            }
            return proceed;
        } catch (Throwable error) {
            TransactionLogging.transactionLogging("Error Transactional call : {0}", thisJoinPoint.getSignature().getName());
            if (transactionManager.isActive()) {
                transactionManager.rollback();                
            }
            throw error;
        } finally {
            if(!transactionManager.isClosed()){
                TransactionLogging.transactionLogging("Removed depth for Transactional call : {0}", thisJoinPoint.getSignature().getName());
                transactionManager.removeTransactionDepth();
            }
        }
    }

    

}