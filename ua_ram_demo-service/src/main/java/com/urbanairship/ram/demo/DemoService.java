package com.urbanairship.ram.demo;

/**
 * Created by ramnamachivayam on 9/12/14.
 */

import com.google.common.util.concurrent.AbstractIdleService;
import com.urbanairship.leatherman.threading.BasicThreadFactory;
import com.urbanairship.reactor.ReactorConfiguration;
import com.urbanairship.reactor.core.server.command.CommandFactory;
import com.urbanairship.reactor.core.server.disco.CuratorDiscoveryProvider;
import com.urbanairship.reactor.core.server.disco.DiscoverableServer;
import com.urbanairship.reactor.core.server.pipeline.CommandChannelPipelineFactory;
import com.urbanairship.reactor.core.server.pipeline.ServerChannelPipelineFactory;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DemoService extends AbstractIdleService {

    private DemoService demoService;
    private final CompositeConfiguration configuration;
    private final ReactorConfiguration reactorConfiguration;

    private static final Logger LOGGER = LogManager.getLogger(DemoService.class);

    private final DiscoverableServer server;
    private final CommandFactory commandFactory;
    private final ExecutorService serviceHost;

    public DemoService(CompositeConfiguration configuration){
        this.configuration = configuration;
        this.reactorConfiguration = new ReactorConfiguration(configuration);

        this.commandFactory = new DemoCommandFactory();

        this.server = createDiscoverableServer(commandFactory, reactorConfiguration);

        this.serviceHost = Executors.newSingleThreadExecutor(new BasicThreadFactory("Demo Reactor Thread"));
    }

    @Override
    protected void startUp() throws Exception {
        //Service service = demoService.startAsync();
        //demoService.startAsync().awaitRunning();
        startReactorServer();
    }

    private void startReactorServer() {
        LOGGER.info("Starting reactor server...");
        serviceHost.submit(server);
        serviceHost.shutdown();
    }

    @Override
    protected void shutDown() throws Exception {
        stopReactorServer();
        //demoService.stopAsync().awaitTerminated();
    }

    private void stopReactorServer() {
        LOGGER.info("Shutting down reactor server...");
        server.shutdown();

        try {
            serviceHost.awaitTermination(10, TimeUnit.SECONDS);
            LOGGER.info("Reactor server shutdown successfully");
        } catch (InterruptedException e) {
            LOGGER.error("Unable to shut reactor server");
        }
    }

    private static DiscoverableServer createDiscoverableServer(CommandFactory commandFactory, ReactorConfiguration reactorConfiguration) {
        ServerChannelPipelineFactory pipelineFactory = new CommandChannelPipelineFactory(reactorConfiguration, commandFactory);
        CuratorDiscoveryProvider discoveryProvider = new CuratorDiscoveryProvider(reactorConfiguration, new AtomicBoolean(true), new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return 0.1;
            }
        });
        return new DiscoverableServer(discoveryProvider, reactorConfiguration, pipelineFactory);
    }
}
