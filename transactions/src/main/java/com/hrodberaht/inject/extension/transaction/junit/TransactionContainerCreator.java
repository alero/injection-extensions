package com.hrodberaht.inject.extension.transaction.junit;

import org.hrodberaht.inject.InjectContainer;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-12 19:45:07
 * @version 1.0
 * @since 1.0
 */
public interface TransactionContainerCreator {
    InjectContainer createContainer();
}
