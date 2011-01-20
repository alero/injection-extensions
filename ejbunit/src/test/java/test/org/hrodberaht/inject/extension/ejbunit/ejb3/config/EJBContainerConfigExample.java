package test.org.hrodberaht.inject.extension.ejbunit.ejb3.config;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.ResourceCreator;
import org.hrodberaht.inject.extension.tdd.ejb.EJBContainerConfigBase;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

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



        String dataSourceName = "MyDataSource";
        if(!ResourceCreator.hasDataSource(dataSourceName)){
            String schemaName = "example-jpa";
            DataSource dataSource = ResourceCreator.createDataSource(schemaName);
            // EntityManager resource
            EntityManager entityManager = ResourceCreator.createEntityManager(schemaName, dataSourceName, dataSource);
            addPersistenceContext(schemaName, entityManager);
            // Named resource
            addResource(dataSourceName, dataSource);
            addSQLSchemas(schemaName, "test/org/hrodberaht/inject/extension/ejbunit");
            // Typed resource
            addResource(DataSource.class, dataSource);

        }
    }



    @Override
    public InjectContainer createContainer() {
        return createAutoScanContainer("test.org.hrodberaht.inject.extension.ejbunit.ejb3.service");
    }







}
