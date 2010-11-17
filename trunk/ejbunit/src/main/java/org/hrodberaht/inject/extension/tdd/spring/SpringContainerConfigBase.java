package org.hrodberaht.inject.extension.tdd.spring;

import org.hrodberaht.inject.extension.tdd.ContainerConfigBase;
import org.hrodberaht.inject.extension.tdd.internal.InjectionRegisterScanBase;
import org.hrodberaht.inject.extension.tdd.spring.internal.InjectionRegisterScanSpring;
import org.hrodberaht.inject.internal.annotation.DefaultInjectionPointFinder;
import org.hrodberaht.inject.spi.InjectionPointFinder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ¤Projectname¤
 *
 * @author Robert Alexandersson
 *         2010-okt-26 18:59:45
 * @version 1.0
 * @since 1.0
 */
public abstract class SpringContainerConfigBase extends ContainerConfigBase<InjectionRegisterScanSpring> {   

    static {
        DefaultInjectionPointFinder finder = new DefaultInjectionPointFinder() {
            @Override
            protected boolean hasInjectAnnotationOnMethod(Method method) {
                // This is a bit special, named configurations must be configured
                // and injected by the post processor inject resources
                return method.isAnnotationPresent(Autowired.class) ||
                        super.hasInjectAnnotationOnMethod(method);
            }

            @Override
            protected boolean hasInjectAnnotationOnField(Field field) {
                // This is a bit special, named configurations must be configured
                // and injected by the post processor inject resources
                return field.isAnnotationPresent(Autowired.class) ||
                        super.hasInjectAnnotationOnField(field);
            }

            @Override
            protected boolean hasPostConstructAnnotation(Method method) {
                // TODO: perhaps not? ...
                return method.isAnnotationPresent(PostConstruct.class) ||
                        super.hasPostConstructAnnotation(method);
            }
        };
        InjectionPointFinder.setInjectionFinder(finder);
    }

    @Override
    protected InjectionRegisterScanBase getScanner() {
        return new InjectionRegisterScanSpring();
    }

    @Override
    protected void injectResources(Object serviceInstance) {
        
    }
}
