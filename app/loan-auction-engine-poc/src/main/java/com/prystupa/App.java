package com.prystupa;

import akka.actor.ActorSystem;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("Hello");

//        final ActorRef script = system.actorOf(TestScript.props());
        system.actorOf(IncomingMessageGateway.props(), "messagingInputGateway");
//        final ActorRef outputGateway = system.actorOf(OutgoingMessageGateway.props(), "outgoingMessagingGateway");
    }
}
