package org.hrodberaht.inject.extension.tdd;

import org.hrodberaht.inject.extension.tdd.ejb.EJBContainerConfigBase;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * Inject extension TDD
 *
 * @author Robert Alexandersson
 *         2011-05-03 21:45
 * @created 1.0
 * @since 1.0
 */
public class JPATestUtil {


    public static void flushAndClear() {
        Collection<EntityManager> entityManagers =
                ((EJBContainerConfigBase)ContainerLifeCycleTestUtil.getThreadConfigBase()).getEntityManagers();
        for(EntityManager entityManager:entityManagers){
            entityManager.flush();
            entityManager.clear();
        }
    }
}
