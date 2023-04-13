package com.devicewifitracker.android.util;

import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class PingTask {

    public static ExecutorService getService(){
        return sService;
    }

    private static ExecutorService sService = Executors.newFixedThreadPool(5, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            thread.setName("PingTask");

            return thread;
        }
    });
}
