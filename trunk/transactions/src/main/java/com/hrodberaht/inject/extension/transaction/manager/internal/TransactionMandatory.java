package com.hrodberaht.inject.extension.transaction.manager.internal;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;

public class TransactionMandatory {

    public TransactionMandatory() {
    }

    Object transactionHandling(ProceedingJoinPoint thisJoinPoint, TransactionManager transactionManager) throws Throwable {
        try {
            if (!transactionManager.isActive()) {
                throw new TransactionHandlingError("TransactionMandatory has no active transaction");                
            }else{
                TransactionLogging.transactionLogging("Added depth for Transactional call : {0}", thisJoinPoint.getSignature().getName());
                transactionManager.addTransactionDepth();
            }
            return thisJoinPoint.proceed();
        } catch (Throwable error) {            
            TransactionLogging.transactionLogging("Error Transactional call : {0}", thisJoinPoint.getSignature().getName());
            if (transactionManager.isActive()) {
                transactionManager.rollback();
            }
            throw error;
        } finally {
            TransactionLogging.transactionLogging("Removed depth for Transactional call : {0}", thisJoinPoint.getSignature().getName());
            transactionManager.removeTransactionDepth();
        }
    }

    

}