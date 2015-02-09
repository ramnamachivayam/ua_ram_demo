package com.urbanairship.ram.demo;


import com.urbanairship.leatherman.signal.SignalHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.urbanairship.leatherman.logging.Logging;
import com.urbanairship.leatherman.configuration.Configuration;
import org.apache.commons.configuration.CompositeConfiguration;
import sun.misc.Signal;

import java.util.concurrent.atomic.AtomicBoolean;


public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main( String[] args ) {

        Logging.configure();
        CompositeConfiguration config = Configuration.create(args);
        config.setProperty("service.name", config.getString("service.name", "DemoService"));
        config.setProperty("service.version", config.getString("service.version", "1.0"));

        final DemoService demoService = new DemoService(config);

        ShutdownHandler shutdownHandler = new ShutdownHandler(demoService);
        SignalHandler.install("TERM", shutdownHandler);
        SignalHandler.install("INT", shutdownHandler);

        demoService.startAndWait();

    }

    private static CompositeConfiguration configureService(String[] args) {
        //Logging.configure();

        CompositeConfiguration config = Configuration.create(args);
        // poor man's setIfAbsent
        config.setProperty("service.name", config.getString("service.name", "DemoService"));
        config.setProperty("service.version", config.getString("service.version", "1.0"));
        return config;
    }

    private static class ShutdownHandler implements SignalHandler.SignalHandlerCallback {

        private final AtomicBoolean shutdownGate = new AtomicBoolean(false);
        private final DemoService demoService;

        public ShutdownHandler(DemoService demoService) {
            this.demoService = demoService;
        }

        @Override
        public boolean handleSignal(Signal signal) {
            if (shutdownGate.compareAndSet(false, true)) {
                LOGGER.info("Shutting down Demo Main...");
                demoService.stopAndWait();
                LOGGER.info("Demo Main fully shutdown");
            } else {
                LOGGER.warn("Already attempting to shutdown");
            }

            return true;
        }
    }

}
