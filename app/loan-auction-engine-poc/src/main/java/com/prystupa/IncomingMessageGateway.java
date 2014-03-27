package com.prystupa;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.camel.CamelMessage;
import akka.camel.javaapi.UntypedConsumerActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 3/16/14
 * Time: 9:34 PM
 */
public class IncomingMessageGateway extends UntypedConsumerActor {

    private Logger logger = LoggerFactory.getLogger(IncomingMessageGateway.class);
    private ActorRef replyGateway;
    private Props serviceProps;

    public static Props props(ActorRef replyGateway, Props service) {
        return Props.create(IncomingMessageGateway.class, () -> new IncomingMessageGateway(replyGateway, service));
    }

    public IncomingMessageGateway(ActorRef replyGateway, Props service) {
        this.replyGateway = replyGateway;
        this.serviceProps = service;
    }

    @Override
    public void onReceive(Object message) throws Exception {

        logger.info("Received message: {}", message);

        if (message instanceof CamelMessage) {
            CamelMessage camelMessage = (CamelMessage) message;
            ActorRef service = context().actorOf(serviceProps);
            service.tell(camelMessage.body(), replyHandler(camelMessage.getHeaders()));
        } else {
            unhandled(message);
        }
    }

    @Override
    public String getEndpointUri() {
        return "rabbitmq://localhost:5672/input?username=guest&password=guest&queue=requests&routingKey=input";
    }

    private ActorRef replyHandler(Map<String, Object> headers) {

        String replyTo = (String) headers.get("rabbitmq.REPLY_TO");
        Props handlerProps = Props.create(ReplyHandler.class, () -> new ReplyHandler(replyTo, replyGateway));
        return context().actorOf(handlerProps);
    }

    private static class ReplyHandler extends UntypedActor {
        private Logger logger = LoggerFactory.getLogger(ReplyHandler.class);
        private String replyTo;
        private ActorRef replyGateway;

        public ReplyHandler(String replyTo, ActorRef replyGateway) {
            this.replyTo = replyTo;
            this.replyGateway = replyGateway;
        }

        @Override
        public void onReceive(Object message) throws Exception {

            if (!(message instanceof CamelMessage)) {
                logger.debug("Received reply: {}", message);

                Map<String, Object> headers = new HashMap<>();
                headers.put("rabbitmq.ROUTING_KEY", replyTo);
                CamelMessage camelMessage = new CamelMessage(message, headers);
                replyGateway.tell(camelMessage, getSelf());
            } else {
                unhandled(message);
            }
        }
    }
}
