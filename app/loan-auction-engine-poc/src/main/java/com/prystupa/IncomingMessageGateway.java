package com.prystupa;

import akka.actor.Props;
import akka.camel.javaapi.UntypedConsumerActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 3/16/14
 * Time: 9:34 PM
 */
public class IncomingMessageGateway extends UntypedConsumerActor {

    private Logger logger = LoggerFactory.getLogger(IncomingMessageGateway.class);

    public static Props props() {
        return Props.create(IncomingMessageGateway.class, IncomingMessageGateway::new);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        logger.info("Received message: {}", message);
    }

    @Override
    public String getEndpointUri() {
        return "rabbitmq://localhost:5672/input?username=guest&password=guest&queue=requests&routingKey=B";
    }
}
