package org.hrodberaht.inject.extension.cdi.inner;

import org.hrodberaht.inject.spi.ResourceCreator;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: alexbrob
 * Date: 2012-11-28
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public class JEEResourceCreator implements ResourceCreator<EntityManager, DataSource> {


    public DataSource createDataSource(String dataSourceName) {

        return null;
    }

    public boolean hasDataSource(String dataSourceName) {
        return true;
    }

    public DataSource getDataSource(String dataSourceName) {
        return null;
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
