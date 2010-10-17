package org.hrodberaht.inject.extension.ejbunit;

import org.hrodberaht.inject.InjectContainer;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.Annotation;

/**
 * ¤Projectname¤
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:32:34
 * @version 1.0
 * @since 1.0
 */
public class EJBJUnitRunner extends BlockJUnit4ClassRunner {

    private InjectContainer theContainer = null;

    private EJBContainerConfigBase creator = null;

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @throws org.junit.runners.model.InitializationError
     *          if the test class is malformed.
     */
    public EJBJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        createContainerFromRegistration();
    }

    private void createContainerFromRegistration() {
        try {
            Class testClass = getTestClass().getJavaClass();
            Annotation[] annotations = testClass.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == EJBContainerContext.class) {
                    EJBContainerContext containerContext = (EJBContainerContext) annotation;
                    Class<? extends EJBContainerConfigBase> transactionClass = containerContext.value();
                    creator = transactionClass.newInstance();
                    theContainer = creator.createContainer();
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
     */
    @Override
    protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        try {

            EJBResourceHandler.begin(creator); 

            try {
                super.runChild(frameworkMethod, notifier);
            } finally {
                ResourceCreator.clearDataSource();
                EJBResourceHandler.end();
            }
        } catch (Throwable e) {
            Description description = describeChild(frameworkMethod);
            notifier.fireTestFailure(new Failure(description, e));
            notifier.fireTestFinished(description);
        }
    }

    /**
     * Runs the injection of dependencies on the test case before returned
     * @return the testcase
     * @throws Exception
     */
    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();
        theContainer.injectDependencies(testInstance);
        creator.injectResources(testInstance);
        return testInstance;
    }

}
