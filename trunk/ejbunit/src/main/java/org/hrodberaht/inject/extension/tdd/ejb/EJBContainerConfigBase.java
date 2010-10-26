package org.hrodberaht.inject.extension.tdd.ejb;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.ejb.internal.DataSourceExecution;
import org.hrodberaht.inject.extension.tdd.ejb.internal.InjectionRegisterScanEJB;
import org.hrodberaht.inject.internal.annotation.DefaultInjectionPointFinder;
import org.hrodberaht.inject.spi.InjectionPointFinder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:35:27
 * @version 1.0
 * @since 1.0
 */
public abstract class EJBContainerConfigBase {

    private static ThreadLocal<InjectionRegisterScanEJB> threadLocal = new ThreadLocal<InjectionRegisterScanEJB>();

    static {
        DefaultInjectionPointFinder finder = new DefaultInjectionPointFinder() {
            @Override
            protected boolean hasInjectAnnotationOnMethod(Method method) {
                return method.isAnnotationPresent(EJB.class) ||
                        super.hasInjectAnnotationOnMethod(method);
            }

            @Override
            protected boolean hasInjectAnnotationOnField(Field field) {
                return field.isAnnotationPresent(EJB.class) ||
                        super.hasInjectAnnotationOnField(field);
            }

            @Override
            protected boolean hasPostConstructAnnotation(Method method) {
                return method.isAnnotationPresent(PostConstruct.class) ||
                        super.hasPostConstructAnnotation(method);
            }
        };
        InjectionPointFinder.setInjectionFinder(finder);
    }

    protected InjectionRegisterScanEJB originalRegister = null;
    protected InjectionRegisterScanEJB activeRegister = null;

    protected Map<String, Object> ejbResources = null;

    public abstract InjectContainer createContainer();

    protected InjectContainer createAutoScanEJBContainer(String packageName) {

        InjectionRegisterScanEJB registerScan = new InjectionRegisterScanEJB();
        registerScan.scanPackage(packageName);

        originalRegister = registerScan;
        activeRegister = originalRegister.clone();
        return activeRegister.getInjectContainer();
    }

    public InjectionRegisterScanEJB getActiveRegister() {
        InjectionRegisterScanEJB registerModule = threadLocal.get();
        if (registerModule == null) {
            threadLocal.set(activeRegister);
            registerModule = activeRegister;
        }
        return registerModule;
    }

    public InjectContainer getActiveContainer() {
        InjectionRegisterScanEJB registerModule = threadLocal.get();
        if (registerModule == null) {
            threadLocal.set(activeRegister);
            registerModule = activeRegister;
        }
        return registerModule.getInjectContainer();
    }

    public void cleanActiveContainer() {
        threadLocal.remove();
        activeRegister = originalRegister;
    }

    protected void addResource(String name, Object value) {
        if (ejbResources == null) {
            ejbResources = new HashMap<String, Object>();
        }
        ejbResources.put(name, value);
    }

    public void addSQLSchemas(String schemaName, String packageBase) {
        DataSourceExecution sourceExecution = new DataSourceExecution();
        sourceExecution.addSQLSchemas(schemaName, packageBase);
    }

    public void injectResources(Object serviceInstance) {
        Field[] declaredFields = serviceInstance.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Resource.class)) {
                Resource resource = field.getAnnotation(Resource.class);
                Object value = ejbResources.get(resource.name());
                if (value != null) {
                    boolean accessible = field.isAccessible();
                    try {
                        if (!accessible) {
                            field.setAccessible(true);
                        }
                        field.set(serviceInstance, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } finally {
                        if (!accessible) {
                            field.setAccessible(false);
                        }
                    }

                }
            }
            if (field.isAnnotationPresent(EJB.class)
                    || field.isAnnotationPresent(Inject.class)) {
                boolean accessible = field.isAccessible();
                try {
                    if (!accessible) {
                        field.setAccessible(true);
                    }
                    injectResources(field.get(serviceInstance));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (!accessible) {
                        field.setAccessible(false);
                    }
                }
            }
        }


    }
}
