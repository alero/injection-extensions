package org.hrodberaht.inject.extension.tdd;

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

    private static final ThreadLocal<ContainerConfigBase> threadLocal = new ThreadLocal<ContainerConfigBase>();

    protected static void begin(ContainerConfigBase theContainer) {
        threadLocal.set(theContainer);
    }

    protected static void end() {
        threadLocal.get().cleanActiveContainer();
        threadLocal.remove();
    }

    public static void registerService(Class serviceDefinition, Class service){
        threadLocal.get().getActiveRegister().overrideRegister(serviceDefinition, service);
    }

    public static void registerServiceInstance(Class serviceDefinition, Object service){
        threadLocal.get().getActiveRegister().overrideRegister(serviceDefinition, service);
    }

    public static void registerModule(RegistrationModule module){
        threadLocal.get().getActiveRegister().register(module);
    }


    public static <T> T getService(Class<T> aClass) {
        ContainerConfigBase containerConfigBase = threadLocal.get();
        T t = containerConfigBase.getActiveContainer().get(aClass);
        containerConfigBase.injectResources(t);
        return t;
    }
}
