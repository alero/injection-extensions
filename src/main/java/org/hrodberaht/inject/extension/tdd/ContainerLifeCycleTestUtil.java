package org.hrodberaht.inject.extension.tdd;

import org.hrodberaht.inject.extension.tdd.internal.ThreadConfigHolder;
import org.hrodberaht.inject.register.RegistrationModule;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-13 00:23:43
 * @version 1.0
 * @since 1.0
 */
public class ContainerLifeCycleTestUtil {



    protected static void begin(ContainerConfigBase theContainer) {
        ThreadConfigHolder.set(theContainer);
    }

    protected static void end() {
        ThreadConfigHolder.get().cleanActiveContainer();
        ThreadConfigHolder.remove();
    }

    public static void registerServiceInstance(Class serviceDefinition, Object service){
        ThreadConfigHolder.get().getActiveRegister().overrideRegister(serviceDefinition, service);
    }

    public static void registerModule(RegistrationModule module){
        ThreadConfigHolder.get().getActiveRegister().register(module);
    }


    public static <T> T getService(Class<T> aClass) {
        ContainerConfigBase containerConfigBase = ThreadConfigHolder.get();
        return containerConfigBase.getActiveContainer().get(aClass);
    }
}
