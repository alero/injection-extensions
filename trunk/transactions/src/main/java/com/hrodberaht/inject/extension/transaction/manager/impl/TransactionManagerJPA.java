package com.hrodberaht.inject.extension.transaction.manager.impl;

import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: Robert Work
 * Date: 2010-aug-16
 * Time: 22:08:43
 * To change this template use File | Settings | File Templates.
 */
public interface TransactionManagerJPA {
    EntityManager getEntityManager();
}
