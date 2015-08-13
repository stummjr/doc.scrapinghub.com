// Might need this if --proxy argument doesn't work
//phantom.casperPath = '<PATH TO CASPERJS>';
//phantom.injectJs(phantom.casperPath +'/bin/bootstrap.js');

var casper = require('casper').create();
// 1. session authentication
casper.start();
casper.setHttpAuth('<API key>', '');  // set up authentication for entire session
casper.start(encodeURI('http://paygo.crawlera.com:8010/fetch?url=http://wtfismyip.com/text'), function(response) {
    this.echo(response.status);  // print response status
});
// or
//  2. call url through authentication and crawlera directly for each url
casper.start(encodeURI('http://<API key>:@paygo.crawlera.com:8010/fetch?url=http://wtfismyip.com/text'), function(response) {
    this.echo(response.status);  // print response status
});

casper.then(function() {
    this.debugHTML();  // print page source
});

casper.run();