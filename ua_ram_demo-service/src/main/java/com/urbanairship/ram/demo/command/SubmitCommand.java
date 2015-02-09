package com.urbanairship.ram.demo.command;

import com.codahale.metrics.Meter;
import com.google.common.base.Objects;
import com.google.protobuf.InvalidProtocolBufferException;
import com.urbanairship.leatherman.metrics.Metrics;
import com.urbanairship.reactor.core.server.command.Command;
import com.urbanairship.reactor.rpc.Reactor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.urbanairship.ram.demo.proto.DemoTweet;
/**
 * Created by ramnamachivayam on 9/12/14.
 */
public class SubmitCommand implements Command<Reactor.Response> {

    private static final Logger LOGGER = LogManager.getLogger(SubmitCommand.class);
    private static final Meter SUBMIT_COMMANDS = Metrics.meter(SubmitCommand.class, "DemoService - submits", "commands");
    private static final Meter SUBMIT_COMMANDS_OK = Metrics.meter(SubmitCommand.class, "DemoService - OK response", "commands");
    //private static final Meter SUBMIT_COMMANDS_REFUSED = Metrics.meter(SubmitCommand.class, "DemoService - refused requests", "commands");
    private static final Meter SUBMIT_COMMANDS_FAILED = Metrics.meter(SubmitCommand.class, "DemoService - failed requests", "commands");
    private static final Meter SUBMIT_COMMAND_PROTOBUF_FAILURES = Metrics.meter(SubmitCommand.class, "DemoService - protobuf errors", "errors");


    private final Reactor.Request request;

    public SubmitCommand(Reactor.Request request) {

        this.request = request;
    }

    @Override
    public Reactor.Response call() throws Exception {

        SUBMIT_COMMANDS.mark();

        Reactor.ResponseCode responseCode = Reactor.ResponseCode.OK;
        DemoTweet.Tweet submitCommand;

        try {
            submitCommand = DemoTweet.Tweet.parseFrom(request.getPayload());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("" + submitCommand.toString());
            }

        } catch (InvalidProtocolBufferException e) {
            SUBMIT_COMMAND_PROTOBUF_FAILURES.mark();
            SUBMIT_COMMANDS_FAILED.mark();
            return Reactor.Response.newBuilder()
                    .setRequestId(request.getRequestId())
                    .setCode(Reactor.ResponseCode.FAIL)
                    .build();
        }
        SUBMIT_COMMANDS_OK.mark();
        return Reactor.Response.newBuilder()
                .setRequestId(request.getRequestId())
                .setCode(responseCode)
                .build();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(SubmitCommand.class)
                .add("request", request.getRequestId())
                .toString();
    }
}
