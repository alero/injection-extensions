package com.hrodberaht.inject.extension.transaction.manager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �Projectname�
 *
 * @author Robert Alexandersson
 *         2010-sep-09 22:01:06
 * @version 1.0
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface NotTransactionJoined {
}
