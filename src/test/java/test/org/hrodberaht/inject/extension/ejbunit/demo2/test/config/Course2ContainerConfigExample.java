package test.org.hrodberaht.inject.extension.ejbunit.demo2.test.config;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.tdd.ejb.EJBContainerConfigBase;

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
        DataSource dataSource = super.createDataSource(dataSourceName);
        super.addResource(dataSourceName, dataSource);

        super.addSQLSchemas(
                "Course2ContainerConfigExample","MyDataSource","test/org/hrodberaht/inject/extension/course2"
        );
    }

    @Override
    public InjectContainer createContainer() {
        return createAutoScanContainer("test.org.hrodberaht.inject.extension.ejbunit.demo2.service");
    }

}
