package com.hrodberaht.inject.extension.transaction;

import org.hrodberaht.inject.register.InjectionFactory;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-06 18:31:10
 * @version 1.0
 * @since 1.0
 */
public interface TransactionManager  {

    boolean begin();
    void commit();
    void rollback();
    boolean isActive();

    void close();
          
    boolean initTransactionHolder();
}
