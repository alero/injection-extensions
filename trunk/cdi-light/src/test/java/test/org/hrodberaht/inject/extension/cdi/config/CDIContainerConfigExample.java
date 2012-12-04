package test.org.hrodberaht.inject.extension.cdi.config;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.CDIContainerConfigBase;
import org.hrodberaht.inject.extension.tdd.ejb.EJBContainerConfigBase;
import org.hrodberaht.inject.spi.ResourceCreator;

import javax.sql.DataSource;
import java.util.Collection;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:37:42
 * @version 1.0
 * @since 1.0
 */
public class CDIContainerConfigExample extends TDDCDIContainerConfigBase {

    public CDIContainerConfigExample() {

        String dataSourceName = "MyDataSource";
        DataSource dataSource = createDataSource(dataSourceName);
        // Named resource
        addResource(dataSourceName, dataSource);

    }

    @Override
    public InjectContainer createContainer() {
        return createAutoScanContainer("test.org.hrodberaht.inject.extension.cdi.service");
    }







}
