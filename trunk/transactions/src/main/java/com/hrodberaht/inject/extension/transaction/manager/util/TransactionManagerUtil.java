package com.hrodberaht.inject.extension.transaction.manager.util;

import com.hrodberaht.inject.extension.transaction.TransactionManager;
import com.hrodberaht.inject.extension.transaction.manager.internal.AspectJTransactionHandler;
import org.hrodberaht.inject.InjectContainer;

/**
 * Injection Transaction Extension
 *
 * @author Robert Alexandersson
 *         2010-aug-27 02:12:40
 * @version 1.0
 * @since 1.0
 */
public class TransactionManagerUtil {

    public static void registerTransactionManager(InjectContainer theContainer){
        if(System.getProperty("transaction.aspectj.disable") == null){
            AspectJTransactionHandler aspectJTransactionHandler =
                    org.aspectj.lang.Aspects.aspectOf(AspectJTransactionHandler.class);
            TransactionManager transactionManager = theContainer.get(TransactionManager.class);
            System.out.println("Connecting the aspect " + aspectJTransactionHandler.toString()
                    + " to transaction manager " + transactionManager);
            theContainer.injectDependencies(aspectJTransactionHandler);
        }
    }


}
