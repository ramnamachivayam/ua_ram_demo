package com.urbanairship.ram.demo;

import com.urbanairship.reactor.core.server.command.Command;
import com.urbanairship.reactor.core.server.command.CommandFactory;
import com.urbanairship.reactor.rpc.Reactor;
import com.urbanairship.ram.demo.proto.DemoTweet;
import com.urbanairship.ram.demo.command.SubmitCommand;
import com.urbanairship.leatherman.metrics.Metrics;
import com.codahale.metrics.Meter;

import java.util.concurrent.TimeUnit;


/**
 * Created by ramnamachivayam on 9/12/14.
 */
public class DemoCommandFactory implements CommandFactory {

    @Override
    public Command getCommand(Reactor.Request request) {
        if (request.getType() != Reactor.RequestType.APPLICATION) {
            throw new IllegalArgumentException("Only requests of type APPLICATION are allowed");
        }

        final DemoTweet.RequestType type = DemoTweet.RequestType.valueOf(request.getApplicationType());
        if (type == null) {
            throw new IllegalArgumentException("Invalid command type sent to Demo");
        }

        switch (type) {
            case SUBMIT:
                return new SubmitCommand(request);
            default:
                throw new IllegalArgumentException("Invalid command type sent to Demo");
        }
    }
}
