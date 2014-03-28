package com.prystupa;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.camel.Camel;
import akka.camel.CamelExtension;
import akka.camel.CamelMessage;
import org.apache.camel.CamelContext;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 3/27/14
 * Time: 6:49 PM
 */
public class ValidatorFilter extends UntypedActor {

    private Logger logger = LoggerFactory.getLogger(ValidatorFilter.class);
    private Cas20ProxyTicketValidator validator = new Cas20ProxyTicketValidator("https://localhost:8443/cas");
    private Props next;

    public static Props props(Props next) {
        return Props.create(ValidatorFilter.class, () -> new ValidatorFilter(next));
    }

    public ValidatorFilter(Props next) {
        this.next = next;

        validator.setAcceptAnyProxy(true);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof CamelMessage) {
            Camel camel = CamelExtension.get(getContext().system());
            CamelContext camelContext = camel.context();
            CamelMessage camelMessage = (CamelMessage) message;
            String ticket = camelMessage.getHeaderAs("ticket", String.class, camelContext);

            Assertion result = validator.validate(ticket, "backend");
            String name = result.getPrincipal().getName();
            logger.debug("Validated incoming request, originated by {}", name);

            getContext().actorOf(next).forward(camelMessage.body(), getContext());
        } else {
            unhandled(message);
        }
    }
}
