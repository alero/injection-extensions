package com.hrodberaht.inject.extension.transaction.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-12 19:47:33
 * @version 1.0
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface InjectionContainerContext {
    Class<? extends InjectionContainerCreator> value();
}
