package com.prystupa;

import akka.actor.Props;
import akka.camel.javaapi.UntypedProducerActor;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 3/20/14
 * Time: 10:30 PM
 */
public class OutgoingMessageGateway extends UntypedProducerActor {

    static Props props() {
        return Props.create(OutgoingMessageGateway.class, OutgoingMessageGateway::new);
    }

    @Override
    public String getEndpointUri() {
        return "rabbitmq://localhost/?username=guest&password=guest";
    }
}
