package com.prystupa;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("Hello");

        final ActorRef outputGateway = system.actorOf(OutgoingMessageGateway.props(), "outgoingMessagingGateway");
        system.actorOf(IncomingMessageGateway.props(outputGateway, ValidatorFilter.props(EchoService.props())), "incomingMessagingGateway");
    }
}
