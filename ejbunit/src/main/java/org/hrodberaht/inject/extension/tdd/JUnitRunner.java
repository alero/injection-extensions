package org.hrodberaht.inject.extension.tdd;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.internal.TransactionManager;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.Annotation;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:32:34
 * @version 1.0
 * @since 1.0
 */
public class JUnitRunner extends BlockJUnit4ClassRunner {

    private InjectContainer activeContainer = null;

    private ContainerConfigBase creator = null;

    /**
     * Creates a BlockJUnit4ClassRunner to run
     *
     * @throws org.junit.runners.model.InitializationError
     *          if the test class is malformed.
     */
    public JUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        createContainerFromRegistration();
    }

    private void createContainerFromRegistration() {
        try {
            Class testClass = getTestClass().getJavaClass();
            Annotation[] annotations = testClass.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == ContainerContext.class) {
                    ContainerContext containerContext = (ContainerContext) annotation;
                    Class<? extends ContainerConfigBase> transactionClass = containerContext.value();
                    creator = transactionClass.newInstance();
                    creator.createContainer();
                }
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param frameworkMethod
     * @param notifier
     */
    @Override
    protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        try {

            TransactionManager.beginTransaction(creator);
            ContainerLifeCycleTestUtil.begin(creator);
            activeContainer = creator.activeRegister.getInjectContainer();

            try {
                super.runChild(frameworkMethod, notifier);
            } finally {
                TransactionManager.endTransaction();
                ContainerLifeCycleTestUtil.end();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Description description = describeChild(frameworkMethod);
            notifier.fireTestFailure(new Failure(description, e));
            notifier.fireTestFinished(description);
        }
    }

    /**
     * Runs the injection of dependencies and resources on the test case before returned
     * @return the testcase
     * @throws Exception
     */
    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();
        activeContainer.injectDependencies(testInstance);
        creator.injectResources(testInstance);
        return testInstance;
    }

}
