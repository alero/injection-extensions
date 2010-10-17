package org.hrodberaht.inject.extension.ejbunit;

import org.hrodberaht.inject.register.RegistrationModule;

/**
 * ¤Projectname¤
 *
 * @author Robert Alexandersson
 *         2010-okt-13 00:23:43
 * @version 1.0
 * @since 1.0
 */
public class EJBResourceHandler {

    private static ThreadLocal<EJBContainerConfigBase> threadLocal = new ThreadLocal<EJBContainerConfigBase>();

    public static void begin(EJBContainerConfigBase theContainer) {
        threadLocal.set(theContainer);
    }

    public static void end() {
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
        return threadLocal.get().getActiveContainer().get(aClass);
    }
}
