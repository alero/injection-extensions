package org.hrodberaht.inject.extension.cdi.inner;

import org.hrodberaht.inject.spi.ResourceCreator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: alexbrob
 * Date: 2012-11-28
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public class JEEResourceCreator implements ResourceCreator<EntityManager, DataSource> {

    private Map<String, DataSource> dataSources = new ConcurrentHashMap<String, DataSource>();
    private InitialContext ctx;

    public JEEResourceCreator() {
        try {
            ctx = new InitialContext();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public DataSource createDataSource(String dataSourceName) {
        DataSource dataSource = dataSources.get(dataSourceName);
        if(dataSource == null){
            try {
                String appServer = System.getProperty("app.server");
                if (appServer != null && appServer.equals("jboss")) {
                    dataSource = (DataSource) ctx.lookup("java:/" + dataSourceName);
                }else{
                    dataSource = (DataSource) ctx.lookup(dataSourceName);
                }
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
            if(dataSource == null){
                throw new IllegalArgumentException("datasource not found"+dataSourceName);
            }
        }
        return dataSource;
    }

    public boolean hasDataSource(String dataSourceName) {
        return dataSources.get(dataSourceName) != null;
    }

    public DataSource getDataSource(String dataSourceName) {
        return dataSources.get(dataSourceName);
    }

    public EntityManager createEntityManager(String schemaName, String dataSourceName, DataSource dataSource) {
        throw new IllegalAccessError("not supported");
    }

    public Collection<EntityManager> getEntityManagers() {
        return new ArrayList<EntityManager>();
    }

    public Collection<DataSource> getDataSources() {
        return new ArrayList<DataSource>();
    }
}
