var amqp = require('amqp');
var logger = require('winston').loggers.get('routes');
var auth = require('../auth');

exports.echo = function (req, res) {

    auth.acquireProxyTicket(req, function (error, ticket) {
        logger.debug('Acquired proxy ticket of %s for %s', ticket, req.originalUrl);

        var conn = amqp.createConnection();
        conn.on('ready', function () {
            logger.debug('AMQP connection is ready');

            conn.exchange('input', {passive: true, confirm: true}, function (exchange) {
                logger.debug('Exchange is ready');

                conn.queue('', function (queue) {
                    logger.debug('AMQP queue is ready: %s', queue.name);

                    queue.subscribe(function (message) {
                        res.end(message.data.toString());
                    });

                    var input = req.params.input;
                    exchange.publish('input', {input: input}, {replyTo: queue.name, headers: {ticket: ticket}}, function () {
                        logger.debug('Published request: %s', input);
                    });
                });
            });
        });
    });
};
