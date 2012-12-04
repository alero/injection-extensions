package test.org.hrodberaht.inject.extension.cdi.config;

import org.hrodberaht.inject.extension.CDIContainerConfigBase;
import org.hrodberaht.inject.extension.tdd.internal.DataSourceExecution;
import org.hrodberaht.inject.extension.tdd.internal.DataSourceProxy;
import org.hrodberaht.inject.extension.tdd.internal.ProxyResourceCreator;
import org.hrodberaht.inject.spi.ResourceCreator;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * User: alexbrob
 * Date: 2012-11-28
 * Time: 10:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class TDDCDIContainerConfigBase extends CDIContainerConfigBase {

    public TDDCDIContainerConfigBase() {
        resourceCreator = new ProxyResourceCreator();
    }

    public TDDCDIContainerConfigBase(ResourceCreator resourceCreator) {
        super(resourceCreator);
    }

    public void addSQLSchemas(String schemaName, String packageBase) {
        DataSourceExecution sourceExecution = new DataSourceExecution(resourceCreator);
        if(!sourceExecution.isInitiated(schemaName, schemaName)){
            sourceExecution.addSQLSchemas(schemaName, packageBase);
        }
    }

    public void addSQLSchemas(String testPackageName, String schemaName, String packageBase) {
        DataSourceExecution sourceExecution = new DataSourceExecution(resourceCreator);
        if(!sourceExecution.isInitiated(testPackageName, schemaName)){
            sourceExecution.addSQLSchemas(schemaName, packageBase);
        }
    }

    public ResourceCreator<EntityManager, DataSourceProxy> getResourceCreator() {
        return resourceCreator;
    }

    protected DataSource createDataSource(String dataSourceName) {
        return resourceCreator.createDataSource(dataSourceName);
    }

    protected boolean hasDataSource(String dataSourceName) {
        return resourceCreator.hasDataSource(dataSourceName);
    }


}
