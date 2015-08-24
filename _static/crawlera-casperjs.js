var casper = require('casper').create();
casper.start();
// always encode url components !
var url_to_crawl = encodeURIComponent('http://wtfismyip.com/text'); // results in http%3A%2F%2Fwtfismyip.com%2Ftext
// You can either
// Authenticate session wide:
casper.setHttpAuth('<API key>', '');
casper.open('http://proxy.crawlera.com:8010/fetch?url=' + url_to_crawl);
// or incorporate authentication into the url:
casper.open('http://<API key>:@proxy.crawlera.com:8010/fetch?url=' + url_to_crawl);

casper.then(function(response) {
    this.echo(response.url);
    this.echo(response.status);
    this.debugHTML();  // print page source
});
casper.run();

