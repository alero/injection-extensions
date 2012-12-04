package org.hrodberaht.inject.extension.inner;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.cdiext.CDIExtensions;
import org.hrodberaht.inject.register.RegistrationModuleAnnotation;
import org.hrodberaht.inject.spi.ContainerConfig;
import org.hrodberaht.inject.spi.InjectionRegisterScanInterface;

import javax.annotation.Resource;
import java.lang.reflect.Field;
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
public abstract class ContainerConfigBase<T extends InjectionRegisterScanBase> implements ContainerConfig {

    protected InjectionRegisterScanInterface originalRegister = null;
    protected InjectionRegisterScanInterface activeRegister = null;

    protected Map<String, Object> resources = null;
    protected Map<Class, Object> typedResources = null;

    private CDIExtensions cdiExtensions = new CDIExtensions();

    public abstract InjectContainer createContainer();

    protected abstract void injectResources(Object serviceInstance);

    protected abstract InjectionRegisterScanInterface getScanner();

    protected InjectContainer createAutoScanContainer(String... packageName) {
        cdiExtensions.runBeforeBeanDiscovery(originalRegister, this);
        InjectionRegisterScanInterface registerScan = getScanner();
        registerScan.scanPackage(packageName);
        originalRegister = registerScan;
        appendTypedResources();
        activeRegister = originalRegister.clone();
        cdiExtensions.runAfterBeanDiscovery(activeRegister, this);
        System.out.println("originalRegister - "+originalRegister);
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

    public InjectionRegisterScanInterface getActiveRegister() {
        return activeRegister;
    }

    public InjectContainer getActiveContainer() {

        return activeRegister.getInjectContainer();
    }

    public void cleanActiveContainer() {
        activeRegister = originalRegister.clone();
        cdiExtensions.runAfterBeanDiscovery(activeRegister, this);
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


    protected boolean injectTypedResource(Object serviceInstance, Field field) {
        if(typedResources == null){
            return false;
        }
        Object value = typedResources.get(field.getType());
        if(value != null){
            injectResourceValue(serviceInstance, field, value);
            return true;
        }
        return false;
    }

    protected boolean injectNamedResource(Object serviceInstance, Field field, Resource resource) {
        if(resources == null){
            return false;
        }
        Object value = resources.get(resource.name());
        if(value == null){
            value = resources.get(resource.mappedName());
        }
        if(value != null){
            injectResourceValue(serviceInstance, field, value);
            return true;
        }
        return false;
    }

    protected void injectResourceValue(Object serviceInstance, Field field, Object value) {
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
}
