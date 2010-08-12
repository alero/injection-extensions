package com.hrodberaht.inject.extension.transaction.junit;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.hrodberaht.inject.InjectContainer;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import javax.ejb.TransactionAttribute;
import java.lang.annotation.Annotation;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-10 20:29:06
 * @version 1.0
 * @since 1.0
 */
public class InjectionJUnitTestRunner extends BlockJUnit4ClassRunner {

    private InjectContainer theContainer = null;
    private TransactionManager transactionManager = null;
    private boolean allMethodsTransacted = false;

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @throws org.junit.runners.model.InitializationError
     *          if the test class is malformed.
     */
    public InjectionJUnitTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        createContainerFromRegistration();
    }

    private void createContainerFromRegistration() {
        try {
            Class testClass = getTestClass().getJavaClass();
            Annotation[] annotations = testClass.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == TransactionContainer.class) {
                    TransactionContainer transactionContainer = (TransactionContainer) annotation;
                    Class<? extends TransactionContainerCreator> transactionclass = transactionContainer.value();
                    TransactionContainerCreator creator = transactionclass.newInstance();
                    theContainer = creator.createContainer();
                    verifyContainerTransactions(theContainer, creator);
                }
                if (annotation.annotationType() == TransactionAttribute.class) {
                    allMethodsTransacted = true;    
                }
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyContainerTransactions(InjectContainer theContainer, TransactionContainerCreator creator) {
        transactionManager = theContainer.get(TransactionManager.class);
        if(transactionManager == null){
            System.out.println("InjectionJUnitTestRunner: " +
                    "TransactionManager not wired for Container from creator: "+creator.getClass().getName());
        }else {
            System.out.println("InjectionJUnitTestRunner: " +
                    "TransactionManager ("+transactionManager.getClass().getSimpleName()+") " +
                    "successfully wired from creator: "+creator.getClass().getSimpleName());
        }
    }

    @Override
	protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        if(hasTransaction(frameworkMethod)){
            transactionManager.begin();
        }
        System.out.println("InjectionJUnitTestRunner: " +
                    " running child " +frameworkMethod.getName());
        try {
            super.runChild(frameworkMethod, notifier);
        } finally{

            if(hasTransaction(frameworkMethod)){
                if(transactionManager.isActive()){
                    transactionManager.rollback();
                }                
            }
        }
    }

    private boolean hasTransaction(FrameworkMethod frameworkMethod) {
        return !hasMethodTransactionDisabled(frameworkMethod) &&
                (allMethodsTransacted || hasMethodTransactionAnnotation(frameworkMethod));
    }

    private boolean hasMethodTransactionDisabled(FrameworkMethod frameworkMethod) {
        if(frameworkMethod.getAnnotation(TransactionDisabled.class) != null){
            return true;    
        }
        return false;
    }

    private boolean hasMethodTransactionAnnotation(FrameworkMethod frameworkMethod) {
        
        return false;
    }

    /**
     * Delegates to the parent implementation for creating the test instance and
     * then allows the container to prepare the test instance before returning it.
     */
    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();
        theContainer.injectDependencies(testInstance);
        return testInstance;
	}
}
