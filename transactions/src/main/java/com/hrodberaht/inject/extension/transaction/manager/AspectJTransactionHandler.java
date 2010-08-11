package com.hrodberaht.inject.extension.transaction.manager;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.ejb.TransactionAttribute;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-10 18:47:12
 * @version 1.0
 * @since 1.0
 */
@Aspect
public class AspectJTransactionHandler {

    private static TransactionManager transactionManager = null;

    public static TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public static void setTransactionManager(TransactionManager transactionManager) {
        AspectJTransactionHandler.transactionManager = transactionManager;
    }

    @Pointcut("execution(@javax.ejb.TransactionAttribute * *(..)) ")
    public void transactionalPointCut(TransactionAttribute transactionAttribute) {

    }

    @Around("transactionalPointCut(transactionAttribute)")
    public Object transactional(ProceedingJoinPoint thisJoinPoint, TransactionAttribute transactionAttribute) throws Throwable {
        try {
            if (!transactionManager.isActive()) {
                System.out.println("Begin Transactional call");
                transactionManager.begin();
            } else {
                System.out.println("Added depth for Transactional call");
                transactionManager.addTransactionDepth();
            }
            Object proceed = thisJoinPoint.proceed();
            if (transactionManager.isActive() && transactionManager.isLastActive()) {
                System.out.println("Commit/Close Transactional call");
                transactionManager.commit();
                transactionManager.close();
            }
            return proceed;
        } catch (Throwable error) {
            System.out.println("Error Transactional call");
            if (transactionManager.isActive()) {
                transactionManager.rollback();
            }
            throw error;
        } finally {

            System.out.println("Removed depth for Transactional call");
            transactionManager.removeTransactionDepth();
        }

    }


}
