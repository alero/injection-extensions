package org.hrodberaht.inject.extension.cdi.inner;

import org.hrodberaht.inject.ScopeContainer;
import org.hrodberaht.inject.internal.exception.InjectRuntimeException;

import javax.ejb.Stateless;
import javax.inject.Singleton;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:45:53
 * @version 1.0
 * @since 1.0
 */
public class InjectionRegisterScanCDI extends InjectionRegisterScanBase {

    @Override
    public InjectionRegisterScanCDI clone() {
        InjectionRegisterScanCDI clone = new InjectionRegisterScanCDI();
        try {
            clone.container = this.container.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    protected boolean isInterfaceAnnotated(Class aClazz) {
        return true;
    }

    @Override
    protected boolean isServiceAnnotated(Class aClazz) {
        return true;
    }

    protected Class findServiceImplementation(Class aClazz, List<Class> listOfClasses) {

        Class foundServiceImplementation = null;
        for(Class aServiceClass:listOfClasses){

            if(!aServiceClass.isInterface()
                    && !aServiceClass.isAnnotation()
                && !Modifier.isAbstract(aServiceClass.getModifiers())
            ){
                for(Class aInterface:aServiceClass.getInterfaces()){
                    if(aInterface == aClazz){
                        if(foundServiceImplementation != null){
                            throw new InjectRuntimeException("ServiceInterface implemented in two classes {0} and {1}"
                                , foundServiceImplementation, aServiceClass
                            );
                        }
                        foundServiceImplementation = aServiceClass;
                    }
                }
            }
        }

        return foundServiceImplementation;
    }

    protected ScopeContainer.Scope getScope(Class serviceClass) {
        if(serviceClass.isAnnotationPresent(Stateless.class)){
            return ScopeContainer.Scope.SINGLETON;
        }
        if(serviceClass.isAnnotationPresent(Singleton.class)){
            return ScopeContainer.Scope.SINGLETON;
        }
        return ScopeContainer.Scope.NEW;
    }

}
