package test.org.hrodberaht.inject.extension.ejbunit.demo2.test.config;

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
public class Course2ContainerConfigExample extends EJBContainerConfigBase {

    public Course2ContainerConfigExample() {

        String dataSourceName = "MyDataSource";
        String jpaName  = "example-jpa";
        DataSource dataSource = super.createDataSource(dataSourceName);
        super.addResource(dataSourceName, dataSource);
        // EntityManager resource
        EntityManager entityManager = createEntityManager(jpaName , dataSourceName, dataSource);
        super.addPersistenceContext(jpaName, entityManager);

        super.addSQLSchemas(dataSourceName, "test/org/hrodberaht/inject/extension/course2");
    }

    @Override
    public InjectContainer createContainer() {
        return createAutoScanContainer("test.org.hrodberaht.inject.extension.ejbunit.demo2.service");
    }

}
