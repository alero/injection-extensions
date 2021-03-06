package org.hrodberaht.inject.extension.tdd;

import org.hrodberaht.inject.ExtendedInjection;
import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.internal.TransactionManager;
import org.hrodberaht.inject.spi.ContainerConfig;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
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
public class JUnitRunnerForArquillian extends org.jboss.arquillian.junit.Arquillian {

    private InjectContainer activeContainer = null;

    private ContainerConfig creator = null;

    private InjectContainer injectContainer = null;

    /**
     * Creates a BlockJUnit4ClassRunner to run
     *
     * @throws org.junit.runners.model.InitializationError
     *          if the test class is malformed.
     */
    public JUnitRunnerForArquillian(Class<?> klass) throws InitializationError {
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
                    Class<? extends ContainerConfig> transactionClass = containerContext.value();
                    creator = transactionClass.newInstance();
                    System.out.println("Creating creator for thread "+Thread.currentThread().toString());
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
            if(injectContainer == null){
                injectContainer = creator.createContainer();
                System.out.println("Creating injectContainer for thread "+Thread.currentThread().toString());
            }
            TransactionManager.beginTransaction(creator);
            ContainerLifeCycleTestUtil.begin(creator);
            activeContainer = creator.getActiveRegister().getInjectContainer();
            try {
                // This will execute the createTest method below, the activeContainer handling relies on this.
                System.out.println("START running test "+
                        frameworkMethod.getName() +" for thread "+Thread.currentThread().toString());
                super.runChild(frameworkMethod, notifier);
                System.out.println("END running test "+
                        frameworkMethod.getName() +" for thread "+Thread.currentThread().toString());
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
        // The active container will automatically inject all normal dependencies and resources
        ((ExtendedInjection)activeContainer).injectExtendedDependencies(testInstance);
        return testInstance;
    }

}
