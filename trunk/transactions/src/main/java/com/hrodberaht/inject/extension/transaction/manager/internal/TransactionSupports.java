package com.hrodberaht.inject.extension.transaction.manager.internal;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;

public class TransactionSupports {

    public TransactionSupports() {
    }

    Object transactionHandling(ProceedingJoinPoint thisJoinPoint, TransactionManager transactionManager) throws Throwable {
        boolean initilizedTx = false;
        try {
            initilizedTx = transactionManager.initTransactionHolder();
            return thisJoinPoint.proceed();
        } catch (Throwable error) {
            // TODO: hmmm rollback for throwing supports?
            TransactionLogging.log("TransactionSupports: Error Transactional call : {0}", thisJoinPoint.getSignature().getName());
            if (transactionManager.isActive()) {
                transactionManager.rollback();                
            }
            throw error;
        }finally{
            if(initilizedTx){
                transactionManager.close();
            }
        }
    }

    

}