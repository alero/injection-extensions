package test.org.hrodberaht.inject.extension.ejbunit.ejb3.config;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.ResourceCreator;
import org.hrodberaht.inject.extension.tdd.ejb.EJBContainerConfigBase;
import org.hrodberaht.inject.extension.tdd.ejb.internal.InjectionRegisterScanEJB;
import org.hrodberaht.inject.extension.tdd.internal.InjectionRegisterScanBase;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:37:42
 * @version 1.0
 * @since 1.0
 */
public class EJBContainerConfigExample extends EJBContainerConfigBase {

    public EJBContainerConfigExample() {
        String dataSourceName = "DataSource";
        if(!ResourceCreator.hasDataSource(dataSourceName)){
            addResource(dataSourceName, ResourceCreator.createDataSource(dataSourceName));
            addSQLSchemas(dataSourceName, "test/org/hrodberaht/inject/extension/ejbunit");
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
