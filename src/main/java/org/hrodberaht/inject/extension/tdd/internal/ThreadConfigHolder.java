package org.hrodberaht.inject.extension.tdd.internal;

import org.hrodberaht.inject.extension.tdd.ContainerConfigBase;

/**
 * Qmatic Booking Module
 *
 * @author Robert Alexandersson
 *         2011-05-22 17:43
 * @created 1.0
 * @since 1.0
 */
public class ThreadConfigHolder {

    private static final ThreadLocal<ContainerConfigBase> threadLocal = new ThreadLocal<ContainerConfigBase>();


    public static ContainerConfigBase get(){
        return threadLocal.get();
    }

    public static void set(ContainerConfigBase configBase){
        threadLocal.set(configBase);
    }

    public static void remove() {
        threadLocal.remove();
    }
}
