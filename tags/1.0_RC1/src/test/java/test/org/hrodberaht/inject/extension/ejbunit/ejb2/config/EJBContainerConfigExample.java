package test.org.hrodberaht.inject.extension.ejbunit.ejb2.config;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.ejb.EJBContainerConfigBase;
import org.hrodberaht.inject.extension.tdd.ejb.internal.InjectionRegisterScanEJB;
import org.hrodberaht.inject.extension.tdd.internal.InjectionRegisterScanBase;

/**
 * �Projectname�
 *
 * @author Robert Alexandersson
 *         2010-okt-26 20:49:29
 * @version 1.0
 * @since 1.0
 */
public class EJBContainerConfigExample extends EJBContainerConfigBase {

    public EJBContainerConfigExample() {
        String dataSourceName = "DataSource";
        if(!hasDataSource(dataSourceName)){
            addResource(dataSourceName, createDataSource(dataSourceName));
            addSQLSchemas(dataSourceName, "test/org/hrodberaht/inject/extension/ejb2unit");
        }
    }




    @Override
    public InjectContainer createContainer() {
        return createAutoScanContainer("test.org.hrodberaht.inject.extension.ejbunit.ejb3.service");
    }




    @Override
    protected InjectionRegisterScanBase getScanner() {
        return new InjectionRegisterScanEJB();
    }
}
