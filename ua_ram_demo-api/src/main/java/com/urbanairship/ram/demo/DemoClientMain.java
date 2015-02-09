package com.urbanairship.ram.demo;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;


/**
 * Created by ramnamachivayam on 9/15/14.
 */
public class DemoClientMain {

    private static final Logger LOGGER = LogManager.getLogger(DemoClientMain.class);
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(500);
        DemoClient dc = new DemoClient();
        executorService.execute(dc);
        executorService.shutdown();
        executorService.awaitTermination(10,TimeUnit.SECONDS);
        LOGGER.info("Finished all threads");
    }
}
