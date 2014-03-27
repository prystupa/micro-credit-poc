/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 3/26/14
 * Time: 11:37 PM
 */

var request = require('request');
var querystring = require('querystring');

module.exports = function (req, res, next) {

    console.log(req.url);

    var cas_host = "https://localhost:8443";
    var opt_service = "https://localhost:3443";

    function validate(ticket) {
        console.log('validating SSO');

        var validateService = "/cas/serviceValidate";
        var query = {'service': opt_service, 'ticket': ticket};
        var ssoUrl = cas_host + validateService
            + '?'
            + querystring.stringify(query);

        request({url: ssoUrl}, function (error, response, body) {

            console.log(body);

            req.session.user = /<cas:user>(.*)<\/cas:user>/.exec(body)[1];
            req.session.ticket = ticket;
            res.writeHead(307, {location: 'https://localhost:3443'});
            return res.end();
        });
    }

    function redirect() {
        console.log('redirecting to SSO');

        var login_service = "/cas/login";
        var queryopts = {'service': opt_service};
        var ssoUrl = cas_host + login_service
            + '?'
            + querystring.stringify(queryopts);

        res.writeHead(307, { 'location': ssoUrl });
        return res.end();
    }

    var ticket = req.param('ticket');
    if (ticket) return validate(ticket);

    var sessionTicket = req.session.ticket;
    if (!sessionTicket) return redirect();
    return next();
};