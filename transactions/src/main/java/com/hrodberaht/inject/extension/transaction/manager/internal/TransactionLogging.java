package com.hrodberaht.inject.extension.transaction.manager.internal;

/**
 * Simple Java Utils
 *
 * @author Robert Alexandersson
 *         2010-aug-11 22:58:13
 * @version 1.0
 * @since 1.0
 */
public class TransactionLogging {
    public static boolean enableLogging = false;
    
    public static void transactionLogging(String message, Object... args) {
        if(enableLogging){
            System.out.println(java.text.MessageFormat.format(message, args));
        }
    }
}
