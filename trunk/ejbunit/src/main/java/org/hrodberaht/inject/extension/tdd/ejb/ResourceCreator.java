package org.hrodberaht.inject.extension.tdd.ejb;

import org.hrodberaht.inject.extension.ejbunit.internal.DataSourceProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit Test EJB (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 20:38:41
 * @version 1.0
 * @since 1.0
 */
public class ResourceCreator {

    
    private static Map<String, DataSourceProxy> DATASOURCES = new HashMap<String, DataSourceProxy>();

    public static void clearDataSource() {
        for(String dbName:DATASOURCES.keySet()){
            DataSourceProxy dataSourceProxy =  DATASOURCES.get(dbName);
            if(dataSourceProxy != null){
                dataSourceProxy.clearDataSource();
            }
        }
    }

    public static DataSourceProxy createDataSource(String dbName) {
        DataSourceProxy dataSourceProxy = new DataSourceProxy(dbName);
        DATASOURCES.put(dbName, dataSourceProxy);
        return dataSourceProxy;
    }

    public static DataSourceProxy getDataSource(String dbName) {        
        return DATASOURCES.get(dbName);
    }



}
