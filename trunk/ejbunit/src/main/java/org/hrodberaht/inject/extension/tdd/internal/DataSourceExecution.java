package org.hrodberaht.inject.extension.tdd.internal;


import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-13 00:15:23
 * @version 1.0
 * @since 1.0
 */
public class DataSourceExecution {

    public static String SCHEMA_PREFIX = "create_schema";
    public static String INSERT_SCRIPT_PREFIX = "insert_script";

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

            DataSourceProxy dataSourceProxy = resourceCreator.getDataSource(schemaName);
            PreparedStatement pstmt = null;
            try {
                Connection connection = dataSourceProxy.getConnection();
                pstmt = connection.prepareStatement(stringBuffer.toString());
                pstmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                } catch (SQLException e) {

                }
                dataSourceProxy.commitDataSource();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isInited(String schemaName) {
        DataSourceProxy dataSourceProxy = resourceCreator.getDataSource(schemaName);
        PreparedStatement pstmt = null;
        try {
            Connection connection = dataSourceProxy.getConnection();
            pstmt = connection.prepareStatement("create table init_control (  control_it integer)");
            pstmt.execute();
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
            dataSourceProxy.commitDataSource();
        }
    }
}
