var amqp = require('amqp');
var logger = require('winston').loggers.get('routes');


exports.echo = function (req, res) {
    var conn = amqp.createConnection();

    conn.on('ready', function () {
        conn.exchange('input', {passive: true, confirm: true}, function (exchange) {
            conn.queue('', function (queue) {
                queue.subscribe(function (message) {
                    res.end(message.data.toString());
                });

                var input = req.params.input;
                exchange.publish('input', {input: input}, {replyTo: queue.name, headers: {ticket: req.session.ticket}}, function () {
                    logger.debug('Published request: %s', input);
                });
            });
        });
    });
};
