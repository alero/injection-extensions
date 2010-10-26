package test.org.hrodberaht.inject.extension.ejbunit.spring.config;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.ResourceCreator;
import org.hrodberaht.inject.extension.tdd.internal.InjectionRegisterScanBase;
import org.hrodberaht.inject.extension.tdd.spring.SpringContainerConfigBase;
import org.hrodberaht.inject.extension.tdd.spring.internal.InjectionRegisterScanSpring;

import javax.sql.DataSource;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:37:42
 * @version 1.0
 * @since 1.0
 */
public class SpringContainerConfigExample extends SpringContainerConfigBase {

    public SpringContainerConfigExample() {
        String dataSourceName = "DataSource";
        if(!ResourceCreator.hasDataSource(dataSourceName)){
            addResource(DataSource.class, ResourceCreator.createDataSource(dataSourceName));
            addSQLSchemas(dataSourceName, "test/org/hrodberaht/inject/extension/ejbunit");
        } else {
            addResource(DataSource.class, ResourceCreator.createDataSource(dataSourceName));
        }
    }

    @Override
    public InjectContainer createContainer() {
        return createAutoScanContainer("test.org.hrodberaht.inject.extension.ejbunit.spring.service");
    }

    @Override
    protected InjectionRegisterScanBase getScanner() {
        return new InjectionRegisterScanSpring();
    }

}
