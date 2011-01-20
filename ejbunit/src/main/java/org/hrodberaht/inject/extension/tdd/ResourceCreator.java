package org.hrodberaht.inject.extension.tdd;

import org.hrodberaht.inject.extension.tdd.internal.DataSourceProxy;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 20:38:41
 * @version 1.0
 * @since 1.0
 */
public class ResourceCreator {

    
    private static Map<String, DataSourceProxy> DATASOURCES = new HashMap<String, DataSourceProxy>();
    private static Map<String, EntityManager> ENTITYMANAGERS = new HashMap<String, EntityManager>();

    public static void clearDataSource() {
        for(String dbName:ENTITYMANAGERS.keySet()){
            EntityManager entityManager =  ENTITYMANAGERS.get(dbName);
            if(entityManager != null){
                entityManager.getTransaction().rollback();
                entityManager.clear();
                // entityManager.close();
            }
        }
        for(String dbName:DATASOURCES.keySet()){
            DataSourceProxy dataSourceProxy =  DATASOURCES.get(dbName);
            if(dataSourceProxy != null){
                dataSourceProxy.clearDataSource();
            }
        }

    }

    public static DataSourceProxy createDataSource(String dbName) {
        DataSourceProxy dataSourceProxy = new DataSourceProxy(dbName);
        DATASOURCES.put(dbName, dataSourceProxy);
        return dataSourceProxy;
    }

    public static DataSourceProxy getDataSource(String dbName) {        
        return DATASOURCES.get(dbName);
    }


    public static boolean hasDataSource(String dataSourceName) {
        return DATASOURCES.get(dataSourceName) != null;
    }

    public static EntityManager createEntityManager(String name, String dataSourceName, DataSource dataSource) {
        registerDataSourceInContext(dataSourceName, dataSource);
        EntityManager entityManager = Persistence.createEntityManagerFactory(name).createEntityManager();
        ENTITYMANAGERS.put(name, entityManager);
        return entityManager;
    }

    private static void registerDataSourceInContext(String dataSourceName, DataSource dataSource) {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.hrodberaht.inject.extension.tdd.ejb.internal.InitialContextFactoryImpl");
        // bind?
        try {
            Context context = new InitialContext();
            context.bind(dataSourceName, dataSource);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void begin() {
        for(String dbName:ENTITYMANAGERS.keySet()){
            EntityManager entityManager =  ENTITYMANAGERS.get(dbName);
            if(entityManager != null){
                entityManager.getTransaction().begin();
                // entityManager.close();
            }
        }
    }
}
