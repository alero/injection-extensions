package com.hrodberaht.inject.extension.transaction.manager.impl;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: Robert Work
 * Date: 2010-aug-18
 * Time: 22:32:22
 * To change this template use File | Settings | File Templates.
 */
public class StatisticsJPA {

    private static AtomicLong beginCount = new AtomicLong(0L);
    private static AtomicLong commitCount = new AtomicLong(0L);
    private static AtomicLong rollbackCount = new AtomicLong(0L);
    private static AtomicLong closeCount = new AtomicLong(0L);

    private static boolean enabled = false;


    public static void setEnabled(boolean enabled) {
        StatisticsJPA.enabled = enabled;
        // Reset
        if (!StatisticsJPA.enabled) {
            beginCount = new AtomicLong(0L);
            commitCount = new AtomicLong(0L);
            rollbackCount = new AtomicLong(0L);
            closeCount = new AtomicLong(0L);
        }
    }

    public static Long getBeginCount() {
        return beginCount.longValue();
    }

    public static Long getCommitCount() {
        return commitCount.longValue();
    }

    public static Long getRollbackCount() {
        return rollbackCount.longValue();
    }

    public static Long getCloseCount() {
        return closeCount.longValue();
    }


    public static void addBeginCount() {
        beginCount.incrementAndGet();
    }

    public static void addCommitCount() {
        commitCount.incrementAndGet();
    }

    public static void addRollbackCount() {
        rollbackCount.incrementAndGet();
    }

    public static void addCloseCount() {
        closeCount.incrementAndGet();
    }
}
