package org.hrodberaht.inject.extension.tdd;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.internal.DataSourceExecution;
import org.hrodberaht.inject.extension.tdd.internal.InjectionRegisterScanBase;
import org.hrodberaht.inject.register.RegistrationModuleAnnotation;

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
public abstract class ContainerConfigBase<T extends InjectionRegisterScanBase> {

    private static ThreadLocal<InjectionRegisterScanBase> threadLocal = new ThreadLocal<InjectionRegisterScanBase>();

    protected InjectionRegisterScanBase originalRegister = null;
    protected InjectionRegisterScanBase activeRegister = null;

    protected Map<String, Object> resources = null;
    protected Map<Class, Object> typedResources = null;

    public abstract InjectContainer createContainer();

    protected abstract void injectResources(Object serviceInstance);

    protected abstract InjectionRegisterScanBase getScanner();

    protected InjectContainer createAutoScanContainer(String packageName) {

        InjectionRegisterScanBase registerScan = getScanner();
        registerScan.scanPackage(packageName);
        originalRegister = registerScan;
        appendTypedResources();
        activeRegister = originalRegister.clone();
        return activeRegister.getInjectContainer();
    }

    private void appendTypedResources() {
        if (typedResources != null) {
            for (final Class typedResource : typedResources.keySet()) {
                final Object value = typedResources.get(typedResource);
                originalRegister.register(new RegistrationModuleAnnotation() {
                    @Override
                    public void registrations() {
                        register(typedResource).withInstance(value);
                    }
                });
            }
        }
    }

    public T getActiveRegister() {
        InjectionRegisterScanBase registerModule = threadLocal.get();
        if (registerModule == null) {
            threadLocal.set(activeRegister);
            registerModule = activeRegister;
        }
        return (T) registerModule;
    }

    public InjectContainer getActiveContainer() {
        InjectionRegisterScanBase registerModule = threadLocal.get();
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
        if (resources == null) {
            resources = new HashMap<String, Object>();
        }
        resources.put(name, value);
    }

    protected void addResource(Class typedName, Object value) {
        if (typedResources == null) {
            typedResources = new HashMap<Class, Object>();
        }
        typedResources.put(typedName, value);
    }


    public void addSQLSchemas(String schemaName, String packageBase) {
        DataSourceExecution sourceExecution = new DataSourceExecution();
        sourceExecution.addSQLSchemas(schemaName, packageBase);
    }


}
