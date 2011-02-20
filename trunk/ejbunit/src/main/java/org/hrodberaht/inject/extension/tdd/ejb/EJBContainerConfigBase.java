package org.hrodberaht.inject.extension.tdd.ejb;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.ContainerConfigBase;
import org.hrodberaht.inject.extension.tdd.ejb.internal.InjectionRegisterScanEJB;
import org.hrodberaht.inject.extension.tdd.internal.InjectionRegisterScanBase;
import org.hrodberaht.inject.internal.annotation.DefaultInjectionPointFinder;
import org.hrodberaht.inject.internal.annotation.ReflectionUtils;
import org.hrodberaht.inject.spi.InjectionPointFinder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:35:27
 * @version 1.0
 * @since 1.0
 */
public abstract class EJBContainerConfigBase extends ContainerConfigBase<InjectionRegisterScanEJB> {

    private Map<String, EntityManager> entityManagers = null;

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

    public abstract InjectContainer createContainer();

    @Override
    protected InjectionRegisterScanBase getScanner() {
        return new InjectionRegisterScanEJB();
    }


    protected void addPersistenceContext(String name, EntityManager entityManager) {
        if(entityManagers == null){
            entityManagers = new HashMap<String, EntityManager>();
        }
        entityManagers.put(name, entityManager);
    }

    protected EntityManager createEntityManager(String schemaName, String dataSourceName, DataSource dataSource) {
        return getResourceCreator().createEntityManager(schemaName, dataSourceName, dataSource);
    }

    protected void injectResources(Object serviceInstance) {
        if (resources == null && entityManagers == null && typedResources == null) {
            return;
        }

        List<Member> members = ReflectionUtils.findMembers(serviceInstance.getClass());
        for (Member member : members) {
            if (member instanceof Field) {
                Field field = (Field) member;
                if (field.isAnnotationPresent(Resource.class)) {
                    Resource resource = field.getAnnotation(Resource.class);
                    if(!injectNamedResource(serviceInstance, field, resource)){
                        injectTypedResource(serviceInstance, field, resource);
                    }
                }
                if (field.isAnnotationPresent(PersistenceContext.class)) {
                    PersistenceContext resource = field.getAnnotation(PersistenceContext.class);
                    injectEntityManager(serviceInstance, field, resource);
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

    private void injectEntityManager(Object serviceInstance, Field field, PersistenceContext resource) {
        Object value = entityManagers.get(resource.unitName());
        if(value == null && entityManagers.size() == 1 && "".equals(resource.unitName())){
            value = entityManagers.values().iterator().next();
        }
        injectResourceValue(serviceInstance, field, value);
    }

    private boolean injectTypedResource(Object serviceInstance, Field field, Resource resource) {
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


    private boolean injectNamedResource(Object serviceInstance, Field field, Resource resource) {
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

    private void injectResourceValue(Object serviceInstance, Field field, Object value) {
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
