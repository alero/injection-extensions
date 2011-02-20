package org.hrodberaht.inject.extension.tdd;

import java.lang.annotation.*;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:34:24
 * @version 1.0
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ContainerContext {
    Class<? extends ContainerConfigBase> value();
}
