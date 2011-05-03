package test.org.hrodberaht.inject.extension.ejbunit.demo.test.config;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.ejb.EJBContainerConfigBase;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * Inject extension TDD
 *
 * @author Robert Alexandersson
 *         2011-05-03 20:31
 * @created 1.0
 * @since 1.0
 */
public class CourseContainerConfigExample extends EJBContainerConfigBase {

    public CourseContainerConfigExample() {

        String dataSourceName = "MyDataSource";
        String schemaName = "example-jpa";
        DataSource dataSource = createDataSource(schemaName);
        // EntityManager resource
        EntityManager entityManager = createEntityManager(schemaName, dataSourceName, dataSource);
        addPersistenceContext(schemaName, entityManager);
        // Named resource
        // addResource(dataSourceName, dataSource);
        addSQLSchemas(schemaName, "test/org/hrodberaht/inject/extension/course");
        // Typed resource
        // addResource(DataSource.class, dataSource);
    }

    @Override
    public InjectContainer createContainer() {
        return createAutoScanContainer("test.org.hrodberaht.inject.extension.ejbunit.demo.service");
    }

}
