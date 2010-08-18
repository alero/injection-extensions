package com.hrodberaht.inject.extension.transaction.junit;

/**
 * Created by IntelliJ IDEA.
 * User: Robert Work
 * Date: 2010-aug-16
 * Time: 23:44:21
 * To change this template use File | Settings | File Templates.
 */
public interface TransactionManagerTest {
    void forceFlush();
    void disableRequiresNew();
    void enableRequiresNew();
}
