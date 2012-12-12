package org.hrodberaht.inject.extension.cdi.cdiext;

import org.hrodberaht.inject.internal.annotation.ReflectionUtils;
import org.hrodberaht.inject.spi.ContainerConfig;
import org.hrodberaht.inject.spi.InjectionRegisterScanInterface;
import org.hrodberaht.inject.spi.ThreadConfigHolder;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: alexbrob
 * Date: 2012-12-03
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
public class CDIExtensions {

    private enum Phase {AfterBeanDiscovery, BeforeBeanDiscovery}

    private Map<Phase, List<MethodClassHolder>> phaseMethods = new ConcurrentHashMap<Phase, List<MethodClassHolder>>();

    public CDIExtensions() {
        phaseMethods.put(Phase.BeforeBeanDiscovery, new ArrayList<MethodClassHolder>());
        phaseMethods.put(Phase.AfterBeanDiscovery, new ArrayList<MethodClassHolder>());
        findExtensions();
    }

    public void runAfterBeanDiscovery(InjectionRegisterScanInterface register, ContainerConfig containerConfig) {
        ThreadConfigHolder.set(containerConfig);
        AfterBeanDiscoveryByInject inject = new AfterBeanDiscoveryByInject(register);
        List<MethodClassHolder> methods = phaseMethods.get(Phase.AfterBeanDiscovery);
        for(MethodClassHolder methodClassHolder:methods){
            try {
                methodClassHolder.getMethod().setAccessible(true);
                Object instance = methodClassHolder.getaClass().newInstance();
                register.getInjectContainer().injectDependencies(instance);
                methodClassHolder.getMethod().invoke(instance, inject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        ThreadConfigHolder.remove();
    }

    public void runBeforeBeanDiscovery(InjectionRegisterScanInterface register, ContainerConfig containerConfig) {
        BeforeBeanDiscoveryByInject inject = new BeforeBeanDiscoveryByInject();
        List<MethodClassHolder> methods = phaseMethods.get(Phase.BeforeBeanDiscovery);
        for(MethodClassHolder methodClassHolder:methods){
            try {
                methodClassHolder.getMethod().setAccessible(true);
                Object instance = methodClassHolder.getaClass().newInstance();
                // Not possible to inject dependencies before the container is built
                // register.getInjectContainer().injectDependencies(instance);
                methodClassHolder.getMethod().invoke(instance, inject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void findExtensions() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/services/javax.enterprise.inject.spi.Extension");
        try {
            File file = new File(url.toURI());
            FileInputStream in = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            StringBuffer stringBuffer = new StringBuffer();
            while ((strLine = br.readLine()) != null) {
                try{
                    evaluateMethodAndPutToCache(strLine);
                }catch (Exception e){

                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void evaluateMethodAndPutToCache(String strLine) throws ClassNotFoundException {
        String classToLookup = strLine;
        classToLookup.trim();
        Class aClass = Class.forName(classToLookup);
        List<Method> methods = ReflectionUtils.findMethods(aClass);
        for(Method method:methods){
            Class[] parameters = method.getParameterTypes();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            if(parameterAnnotations.length != 1){
                if(parameterAnnotations[0].length != 1){
                    return;
                }
            }
            for(int i=0;i<parameters.length;i++){
                Class parameter = parameters[i];
                if(parameterAnnotations[0][0].annotationType() == Observes.class){
                    if(parameter.equals(AfterBeanDiscovery.class)){
                        this.phaseMethods.get(Phase.AfterBeanDiscovery).add(new MethodClassHolder(aClass, method));
                    }

                    if(parameter.equals(BeforeBeanDiscovery.class)){
                        this.phaseMethods.get(Phase.BeforeBeanDiscovery).add(new MethodClassHolder(aClass, method));
                    }
                }
            }

        }
    }


}
