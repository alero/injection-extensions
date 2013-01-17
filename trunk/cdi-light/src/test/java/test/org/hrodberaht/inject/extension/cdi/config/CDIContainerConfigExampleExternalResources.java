package test.org.hrodberaht.inject.extension.cdi.config;

import org.hrodberaht.inject.InjectContainer;

import javax.sql.DataSource;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:37:42
 * @version 1.0
 * @since 1.0
 */
public class CDIContainerConfigExampleExternalResources extends TDDCDIContainerConfigBase {

    public CDIContainerConfigExampleExternalResources() {

        String dataSourceName = "MyDataSource";
        DataSource dataSource = createDataSource(dataSourceName);
        // Named resource
        addResource(dataSourceName, dataSource);

    }

    @Override
    public InjectContainer createContainer() {
        return createAutoScanContainer("org.hrodberaht.inject.extension.cdi.example.service");
    }







}
