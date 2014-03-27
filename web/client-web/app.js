/**
 * Module dependencies.
 */

var express = require('express');
var http = require('http');
var https = require('https');
var path = require('path');
var fs = require('fs');

var app = express();
var auth = require('./auth');

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', path.join(__dirname, 'views'));

app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());

app.use(express.cookieParser('your secret here'));
app.use(express.session());

app.use(auth);


app.get('/username', function (req, res) {
    var user = req.session.user;
    res.end(user);
});

app.get('/echo/:input', function (req, res) {
    res.end(req.params.input);
});

app.use(app.router);
app.use(require('stylus').middleware(path.join(__dirname, 'public')));

app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
    app.use(express.errorHandler());
}

http.createServer(app).listen(app.get('port'), function () {
    console.log('Express server listening on port ' + app.get('port'));
});

// This line is from the Node.js HTTPS documentation.
var options = {
    key: fs.readFileSync('key.pem'),
    cert: fs.readFileSync('cert.pem')
};
// Create an HTTPS service identical to the HTTP service.
https.createServer(options, app).listen(3443, function () {
    console.log('Express server listening on port ' + 3443);
});

process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";