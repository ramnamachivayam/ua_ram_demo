package com.urbanairship.ram.demo;

/**
 * Created by ramnamachivayam on 9/15/14.
 */

import com.urbanairship.ram.demo.proto.DemoTweet;
import com.urbanairship.reactor.core.client.ClientConfiguration;
import com.urbanairship.reactor.core.client.async.ReactorFuture;
import com.urbanairship.reactor.core.client.async.netty.DynamicNettyAsyncClient;
import com.urbanairship.reactor.rpc.Reactor;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DemoClient implements Runnable{


    private static final Logger LOGGER = LogManager.getLogger(DemoClient.class);

    private DynamicNettyAsyncClient client;

    @Override
    public void run() {
        try {
            long startTime = System.nanoTime();
            ClientConfiguration config = new ClientConfiguration();
            client = new DynamicNettyAsyncClient("DemoService", "1.0", new ClientConfiguration(new SystemConfiguration(), "DemoService"));
            //client = new SynchronousClientImpl(config);
            for(int i=0;i<50000;i++) {
            //while(true){

                DemoTweet.Tweet.Builder demoBuilder = DemoTweet.Tweet.newBuilder().setTweetid(100).setTweetmsg("welcome");

                //DemoTweet.Tweet tweet = demoBuilder.build();

                Reactor.Request request = Reactor.Request.newBuilder()
                        .setTimestamp(System.currentTimeMillis())
                        .setRequestId(1001)
                        .setType(Reactor.RequestType.APPLICATION)
                        .setPayload(demoBuilder.build().toByteString())
                        .setApplicationType(DemoTweet.RequestType.SUBMIT_VALUE)
                        .build();

                ReactorFuture<Reactor.Response> reactorResponse = client.send("DemoService", request);

                LOGGER.debug("Response from Service :" + reactorResponse.get().getRequestId() );

             //  LOGGER.info(reactorResponse.get().getCode() + " " + (System.nanoTime() - startTime));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception while sending to reactor endpoint " + client.toString(), e);
        }
    }


}
