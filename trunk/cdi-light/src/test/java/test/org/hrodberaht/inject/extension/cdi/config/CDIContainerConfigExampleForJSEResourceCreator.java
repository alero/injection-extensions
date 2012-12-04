package test.org.hrodberaht.inject.extension.cdi.config;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.CDIContainerConfig;
import org.hrodberaht.inject.extension.tdd.internal.DataSourceProxy;
import org.hrodberaht.inject.spi.ResourceCreator;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:37:42
 * @version 1.0
 * @since 1.0
 */
public class CDIContainerConfigExampleForJSEResourceCreator extends CDIContainerConfig {

    public CDIContainerConfigExampleForJSEResourceCreator() {

        String dataSourceName = "MyDataSource";
        DataSource dataSource = createDataSource(dataSourceName);
        // Named resource
        addResource(dataSourceName, dataSource);

        addSQLSchemas(dataSourceName, "test");

    }

    public void addSQLSchemas(String schemaName, String packageBase) {
        DataSourceExecution sourceExecution = new DataSourceExecution(resourceCreator);
        if (!sourceExecution.isInitiated(schemaName, schemaName)) {
            sourceExecution.addSQLSchemas(schemaName, packageBase);
        }
    }


    @Override
    public InjectContainer createContainer() {
        return createAutoScanContainer("test.org.hrodberaht.inject.extension.cdi.service2");
    }


    private class DataSourceExecution {

        public String SCHEMA_PREFIX = "create_schema";
        public String INSERT_SCRIPT_PREFIX = "insert_script";

        private ResourceCreator resourceCreator;


        public DataSourceExecution(ResourceCreator resourceCreator) {
            this.resourceCreator = resourceCreator;
        }

        public void addSQLSchemas(String schemaName, String packageBase) {

            URL url = Thread.currentThread().getContextClassLoader().getResource(packageBase);
            String directoryString = url.getFile().replaceAll("%20", " ");
            File directory = new File(directoryString);
            File[] files = directory.listFiles();
            runScripts(files, schemaName, SCHEMA_PREFIX);
            runScripts(files, schemaName, INSERT_SCRIPT_PREFIX);
        }

        private void runScripts(File[] files, String schemaName, String prefix) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith(prefix)) {
                    executeScript(file, schemaName);
                }
            }
        }

        private void executeScript(File file, String schemaName) {
            FileInputStream fstream = null;
            try {
                fstream = new FileInputStream(file);

                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                StringBuffer stringBuffer = new StringBuffer();
                while ((strLine = br.readLine()) != null) {
                    stringBuffer.append(strLine);
                }

                DataSource dataSourceProxy = (DataSource) resourceCreator.getDataSource(schemaName);
                Statement stmt = null;
                try {
                    Connection connection = dataSourceProxy.getConnection();
                    stmt = connection.createStatement();
                    stmt.execute(stringBuffer.toString());
                    connection.commit();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    } catch (SQLException e) {

                    }

                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        public boolean isInitiated(String testPackageName, String schemaName) {
            DataSource dataSourceProxy = resourceCreator.getDataSource(schemaName);
            PreparedStatement pstmt = null;
            try {
                if (dataSourceProxy == null) {
                    throw new IllegalAccessError("schemaName:" + schemaName + " does not exist ");
                }
                Connection connection = dataSourceProxy.getConnection();
                pstmt = connection.prepareStatement("create table " + testPackageName + " (  control_it integer)");
                pstmt.execute();
                connection.commit();
                return false;
            } catch (SQLException e) {
                return true;
            } finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                } catch (SQLException e) {

                }

            }
        }
    }


}
