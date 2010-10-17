package org.hrodberaht.inject.extension.ejbunit.internal;

import org.hrodberaht.inject.InjectionRegisterModule;
import org.hrodberaht.inject.ScopeContainer;
import org.hrodberaht.inject.internal.exception.InjectRuntimeException;
import org.hrodberaht.inject.register.RegistrationModuleAnnotation;

import javax.ejb.Local;
import javax.ejb.Remote;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * ¤Projectname¤
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:45:53
 * @version 1.0
 * @since 1.0
 */
public class InjectionRegisterScanEJB extends InjectionRegisterModule {

    private boolean detailedScanLogging = false;


    public InjectionRegisterScanEJB scanPackage(String packagename) {
        Class[] clazzs = getClasses(packagename);
        List<Class> listOfClasses = new ArrayList<Class>(clazzs.length);
        for (Class aClazz : clazzs) {
            listOfClasses.add(aClazz);
        }
        for (Class aClazz : clazzs) {
            createRegistration(aClazz, listOfClasses);
        }
        return this;
    }

    public InjectionRegisterScanEJB scanPackage(String packagename, Class... manuallyexcluded) {
        Class[] clazzs = getClasses(packagename);
        List<Class> listOfClasses = new ArrayList<Class>(clazzs.length);

        // remove the manual excludes
        for (Class aClazz : clazzs) {
            if (!manuallyExcluded(aClazz, manuallyexcluded)) {
                listOfClasses.add(aClazz);
            }
        }
        for (Class aClazz : listOfClasses) {
            createRegistration(aClazz, listOfClasses);
        }
        return this;
    }

    public void setDetailedScanLogging(boolean detailedScanLogging) {
        this.detailedScanLogging = detailedScanLogging;
    }

    private void createRegistration(Class aClazz, List<Class> listOfClasses) {
        if (
                aClazz.isInterface()
                && !aClazz.isAnnotation()
                && isEJBInterfaceAnnotated(aClazz)
                ) {
            try{
                Class serviceClass = findEJBServiceImplementation(aClazz, listOfClasses);
                register(aClazz, serviceClass, getScope(serviceClass));
            }catch(InjectRuntimeException e){
                System.out.println("Hrodberaht Injection: Silently failed to register class = "+aClazz);
                if(detailedScanLogging){
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    private Class findEJBServiceImplementation(Class aClazz, List<Class> listOfClasses) {

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

    private boolean isEJBInterfaceAnnotated(Class aClazz) {
        if(aClazz.isAnnotationPresent(Local.class)){
            return true;
        }else if(aClazz.isAnnotationPresent(Remote.class)){
            return true;
        }
        return false;
    }

    private ScopeContainer.Scope getScope(Class serviceClass) {
        // Its a nice thought but for Unit tests we need the container to reform all services at all times
        /*if(serviceClass.isAnnotationPresent(Stateless.class)){
            return ScopeContainer.Scope.SINGLETON;
        }*/
        return ScopeContainer.Scope.NEW;
    }

    private boolean manuallyExcluded(Class aClazz, Class[] manuallyexluded) {
        for (Class excluded : manuallyexluded) {
            if (excluded == aClazz) {
                return true;
            }
        }
        return false;
    }


    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     */
    private Class[] getClasses(String packageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ArrayList<Class> classes = findFiles(packageName, classLoader);
        return classes.toArray(new Class[classes.size()]);
    }

    private ArrayList<Class> findFiles(String packageName, ClassLoader classLoader) {
        ArrayList<Class> classes = new ArrayList<Class>();
        try {

            assert classLoader != null;
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile().replaceAll("%20"," ")));
            }

            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
        } catch (IOException e) {
            throw new InjectRuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new InjectRuntimeException(e);
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(
                        Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6))
                );
            }
        }
        return classes;
    }

    @Override
    public InjectionRegisterScanEJB clone() {
        InjectionRegisterScanEJB clone = new InjectionRegisterScanEJB();
        try {
            clone.container = this.container.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    public void overrideRegister(final Class serviceDefinition, final Object service) {
        RegistrationModuleAnnotation registrationModule = new RegistrationModuleAnnotation(){
            @Override
            public void registrations() {
                register(serviceDefinition).withInstance(service);
            }
        };
        container.register(registrationModule);
    }
}
