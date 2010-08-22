package com.hrodberaht.inject.extension.transaction.jdbc;

/**
 * Spring extensions
 *
 * @author Robert Alexandersson
 * @version 1.0
 * @since 1.0
 */
public interface InsertOrUpdater extends Insert{
    
    InsertOrUpdater where(String name, Object value);


}
