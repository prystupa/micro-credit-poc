package com.prystupa;

import akka.actor.Props;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 3/22/14
 * Time: 10:37 AM
 */
public class EchoService extends UntypedActor {

    private Logger logger = LoggerFactory.getLogger(EchoService.class);

    static Props props() {
        return Props.create(EchoService.class, EchoService::new);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        logger.info("Received message: {}", message);
        getSender().tell(message, getSelf());
    }
}
