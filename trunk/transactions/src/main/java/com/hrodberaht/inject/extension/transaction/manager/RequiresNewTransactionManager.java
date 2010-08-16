package com.hrodberaht.inject.extension.transaction.manager;

/**
 * Created by IntelliJ IDEA.
 * User: Robert Work
 * Date: 2010-aug-15
 * Time: 19:32:15
 * To change this template use File | Settings | File Templates.
 */
public interface RequiresNewTransactionManager {
    void newBegin();
    void newCommit();
    void newRollback();

    boolean newIsActive();

    void newClose();
}
