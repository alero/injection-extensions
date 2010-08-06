package com.hrodberaht.inject.extension.jsf;

import org.hrodberaht.inject.InjectContainer;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-jul-26 22:58:42
 * @version 1.0
 * @since 1.0
 */
public class JsfInjectionProvider extends JsfInjectionProviderBase{

    private static InjectContainer container = null;
    public static void setInjector(InjectContainer container) {
        JsfInjectionProvider.container = container;
    }

    @Override
    public InjectContainer getContainer() {
        return JsfInjectionProvider.container;
    }


}
