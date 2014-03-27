/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 3/26/14
 * Time: 11:37 PM
 */

var logger = require('winston').loggers.get('auth');
var request = require('request');
var querystring = require('querystring');

var cas_host = "https://localhost:8443";
var opt_service = "https://localhost:3443";

function checkAndLogin(req, res, next) {
    logger.debug('Checking auth for %s', req.originalUrl);

    function validate(ticket) {
        logger.debug('Validating ticket', {ticket: ticket});

        var validateService = "/cas/serviceValidate";
        var query = {'service': opt_service, 'ticket': ticket};
        var ssoUrl = cas_host + validateService
            + '?'
            + querystring.stringify(query);

        request({url: ssoUrl}, function (error, response, body) {

            req.session.user = /<cas:user>(.*)<\/cas:user>/.exec(body)[1];
            req.session.ticket = ticket;
            res.writeHead(307, {location: 'https://localhost:3443'});
            return res.end();
        });
    }

    function redirect() {
        logger.debug('Redirecting to SSO');

        var login_service = "/cas/login";
        var query = {'service': opt_service};
        var ssoUrl = cas_host + login_service
            + '?'
            + querystring.stringify(query);

        res.writeHead(307, { 'location': ssoUrl });
        return res.end();
    }

    var ticket = req.param('ticket');
    if (ticket) return validate(ticket);

    var sessionTicket = req.session.ticket;
    if (!sessionTicket) return redirect();
    return next();
}

function logout(req, res) {

    var logoutService = "/cas/logout";
    var ssoUrl = cas_host + logoutService;

    req.session.destroy();

    res.writeHead(307, { 'location': ssoUrl });
    return res.end();
}

module.exports = {
    checkAndLogin: checkAndLogin,
    logout: logout
};