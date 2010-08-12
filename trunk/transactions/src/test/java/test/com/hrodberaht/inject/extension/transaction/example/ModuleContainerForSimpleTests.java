package test.com.hrodberaht.inject.extension.transaction.example;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.InjectionRegisterModule;

public class ModuleContainerForSimpleTests implements com.hrodberaht.inject.extension.transaction.junit.InjectionContainerCreator {
    public ModuleContainerForSimpleTests() {
    }

    public InjectContainer createContainer() {
        InjectionRegisterModule register = new InjectionRegisterModule();
        register.activateContainerJavaXInject();                
        
        return register.getInjectContainer();
    }
}