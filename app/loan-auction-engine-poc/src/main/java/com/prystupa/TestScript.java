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
public class TestScript extends UntypedActor {

    private Logger logger = LoggerFactory.getLogger(TestScript.class);

    static Props props() {
        return Props.create(TestScript.class, TestScript::new);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        logger.debug("receive {}", message);
    }
}
