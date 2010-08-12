package com.hrodberaht.inject.extension.transaction.junit;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.internal.exception.InjectRuntimeException;
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
 *
 * TransactionAttribute will enable the class or a method to be "TestCase-Transactional"
 * The no need for a type, they will all be REQUIRED with rollback as the end call.
 * The BEGIN/ROLLBACK will always be enforced if the test is classified as "TestCase-Transactional"
 *
 * TIP1: To make an entire class transactional use @TransactionAttribute as a class type annotation
 * TIP1a: If the class is transactional, to disable a single test to be non transactional
 *        use the @TransactionDisabled annotation for that method.
 *
 * TIP2: To enable transaction support for a single method just use the @TransactionAttribute for that method.
 *
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
                if (annotation.annotationType() == InjectionContainerContext.class) {
                    InjectionContainerContext transactionContainer = (InjectionContainerContext) annotation;
                    Class<? extends InjectionContainerCreator> transactionclass = transactionContainer.value();
                    InjectionContainerCreator creator = transactionclass.newInstance();
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

    /**
     * Each test is verified for transaction support.
     * To enable a single methods for transaction use the @TransactionAttribute
     * Disabled TransactionDisabled comes first and will always disable (even if a TransactionAttribute exists)
     *
     * @param frameworkMethod
     * @param notifier
     * @see TransactionDisabled
     * @see TransactionAttribute
     */
    @Override
    protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        if (hasTransaction(frameworkMethod)) {
            transactionManager.begin();
        }
        System.out.println("InjectionJUnitTestRunner: " +
                " running child " + frameworkMethod.getName());
        try {
            super.runChild(frameworkMethod, notifier);
        } finally {

            if (hasTransaction(frameworkMethod)) {
                if (transactionManager.isActive()) {
                    transactionManager.rollback();
                }
            }
        }
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


    private boolean hasTransaction(FrameworkMethod frameworkMethod) {
        return !hasMethodTransactionDisabled(frameworkMethod) &&
                (allMethodsTransacted || hasMethodTransactionAnnotation(frameworkMethod));
    }

    private boolean hasMethodTransactionDisabled(FrameworkMethod frameworkMethod) {
        if (frameworkMethod.getAnnotation(TransactionDisabled.class) != null) {
            return true;
        }
        return false;
    }

    private boolean hasMethodTransactionAnnotation(FrameworkMethod frameworkMethod) {

        return false;
    }

    private void verifyContainerTransactions(InjectContainer theContainer, com.hrodberaht.inject.extension.transaction.junit.InjectionContainerCreator creator) {
        try {
            transactionManager = theContainer.get(TransactionManager.class);
            System.out.println("InjectionJUnitTestRunner: " +
                    "TransactionManager (" + transactionManager.getClass().getSimpleName() + ") " +
                    "successfully wired from creator: " + creator.getClass().getSimpleName());

        } catch (InjectRuntimeException exception) {
            System.out.println("InjectionJUnitTestRunner: " +
                    "TransactionManager not wired for Container from creator: " + creator.getClass().getName());
        }
    }
}
