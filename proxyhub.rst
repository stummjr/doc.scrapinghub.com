.. _proxyhub:

=========
Proxy Hub
=========

Overview
========

ProxyHub provides an HTTP proxy, with a pool of rotating IPs, designed
specifically for scraping purposes. Although it provides a standard HTTP proxy
interface it does a lot more internally, like throttling access to domains by
introducing delays and discarding IPs from the pool when they get banned or
have other problems. As a scraping user, you no longer have to worry about
tinkering with download delays, concurrent requests, user agents, cookies or
referrers to avoid getting banned from sites, you just configure the proxy and
fire up your crawler of choice and start scraping.

How does it work?
=================

ProxyHub architecture is based on a group of “master” proxies that receive the
user requests and distribute them among many internal “slave” proxies.

The internal slave proxies are just plain simple HTTP proxies (like one you
would run with Squid_ or similar software) with no extra logic.

The masters, however, implement a proprietary algorithm to minimize the risks
of getting banned, by throttling requests sent to sites from each internal
slave, among other techniques. If, for whatever reason, any slaves do get
banned anyway, the masters will detect and avoid using them in the future for
those particular sites.

Banned requests typically return a non-200 response (usually 503 or 403) or
will redirect to a captcha page. These responses are detected by ProxyHub
masters and the requests are automatically retried from other (clean) slaves.
Banned slaves are blacklisted to prevent using them again for that domain. All
this logic happens inside ProxyHub infrastructure and the user never receives
the banned response, nor is charged for them (only successful requests are
charged). The user may get a 503 response from ProxyHub if there are no more
clean slaves left to try for a particular domain. In this case, the user may
choose to reserve dedicated slaves to increase the capacity. Also, thanks to
how ProxyHub architecture works, users can supply their own proxies to be used
as slave. See Pricing for more details.

Pricing
=======

Unlike other proxy providers, ProxyHub offers a simple pricing model, where you
only pay for successful requests. This means that you will not be charged if
there is no capacity for processing your request from a clean slave.  You also
have the option to reserve additional slave proxies to use exclusively for your
requests (thus preventing other customer requests interfering with yours) or
even supply your own proxies to the pool of slaves.


Monitoring Usage
================

ProyHub provides usage reports with details of how many requests were performed
by day and month. You can view them by logging into `the panel`_ and clicking
on the ProxyHub item in the top navigation bar.

.. note:: The graphs are updated once a day (at around 6am UTC).

Using ProxyHub with curl
========================

ProxyHub provides a standard HTTP proxy interface so you can use it with any
software that supports them.

Here is an example using ProxyHub with curl to download
http://scrapinghub.com::

    curl -x proxy.scrapinghub.com:8010 -U USER:PASS http://scrapinghub.com

Where ``USER`` and ``PASS`` should be replaced by the credentials you got when
you signed up for the ProxyHub service.

Using ProxyHub with other unix commands
=======================================

Several Unix commands (like ``wget`` and ``curl``) and applications (including
Scrapy) support the ``http_proxy`` environment variable to configure the HTTP
proxy to use.

You can configure before running your command with::

    export http_proxy=http://USER:PASS@proxy.scrapinghub.com:8010

Using ProxyHub with Scrapy
==========================

To use ProxyHub with Scrapy you could just set the ``http_proxy`` environment
setting (as explained in the previous section). However, if you need more
functionality (like configuring which spiders to send through the proxy) you
can use the  `HubProxyMiddleware`_, provided in the `scrapylib` project.

Download the `scrapylib` project, and enable the middleware by adding this to
your Scrapy settings::

    DOWNLOADER_MIDDLEWARES = {'scrapylib.hubproxy.HubProxyMiddleware': 600}

**Important note**: The logic of `HubProxyMiddleware`_ controlled by the parameter ``maxbans`` (see below)
will not work if this middleware is placed behind (from the downloader point of view) of the `RetryMiddleware`, so its positional
value must be higher than the last. A position number of 600 is ok under current defaults.

Then configure your HubProxy username and password by adding following
settings::

    HUBPROXY_USER = 'john'
    HUBPROXY_PASS = 'secret'


Finally, set the ``use_proxy`` attributes in the spiders that you want to use
ProxyHub. For example::

    class MySpider(BaseSpider):

        start_urls = ['...']
        use_hubproxy = True

        # ...

Alternativelly, you can set ``HUBPROXY_ENABLED`` to *True* in the spider (or global) scrapy settings section in the scrapinghub panel.

Enabling ProxyHub in Panel
==========================

ProxyHub can easily be enabled for a given spider using the Panel user interface. Once we provided you the required user and password
for using ProxyHub, follow these steps:

1. Go to your project addons control interface, and add ProxyHub addon.
2. Go to Proxyhub addon settings, and add the user and password we will provide you (with settings ``HUBPROXY_USER`` and ``HUBPROXY_PASS``).
3. For each spider you need to use ProxyHub, go to its settings control, add the setting ``HUBPROXY_ENABLED`` and switch it on (ensure you are using the spider settings interface, not the project one, unless you really want to enable ProxyHub for every spider in your project. Remember it usage generates extra price charge)

You can also follow these steps in order to setup up your own proxy server. In that case you have also to change the default value of the
setting ``HUBPROXY_URL``.

Extra settings
==============

The `HubProxyMiddleware`_ allows to control the maximal delay that the scrapy downloader will wait for the response to a request sent
to the ProxyHub, before raising a timeout exception. Usually you will need to set this timeout to a bigger value than default one, as
ProxyHub long latencies has a different meaning that long latencies that yields from crawling the target site directly. Under some
situations where many proxy slaves are banned by the target site, the proxyHub need to try many slaves before finding a suitable
one, and in such cases its response latency could be quite important the first tries.

This timeout delay can be controlled by the setting ``HUBPROXY_DOWNLOAD_TIMEOUT`` or spider attribute ``hubproxy_download_timeout``,
and its default value is 1800 seconds.

Another important parameter is the maximal number of banned status responses returned by the ProxyHub that will be accepted
before giving up and close the spider. This parameter is set up with the setting ``HUBPROXY_MAXBANS`` or spider attribute
``hubproxy_maxbans``.

Frequently Asked Questions
==========================

How does ProxyHub compare to other proxy providers?
---------------------------------------------------

Other proxy vendors typically provide a pool of IPs running simple HTTP proxies
(often using Squid_) whereas ProxyHub provides a single master proxy that
distributes the requests among many slaves, keeps track of which slaves are
banned (per domain), and throttles the requests to make sure no domain is hit
too fast from a single IP, thus minimizing the risk of your crawler getting
banned or causing serious problems for the websites being crawled.

With other proxy providers, you have to implement the throttling yourself,
while with ProxyHub you only configure your crawler to use the proxy and let
ProxyHub deal with the throttling: you just fire off your crawler with no
delays, and the proxy will crawl as fast as it cans.</p>

How many IPs does ProxyHub provide?
-----------------------------------

It varies and it's not very relevant, as long as it stays above a certain
threshold (which we always guarantee).

This is a very common question because, in most proxy providers, the number of
IPs determine how fast you can crawl sites, which is often what you want to
find out when you ask that question. However, ProxyHub is different. The number
of IPs doesn't matter much because it is ProxyHub (not the user) which
throttles speed and request delays (to prevent users getting banned) and there
is an imposed global limit on how fast any single site can be crawled through
ProxyHub, so adding more IPs (above a certain threshold) won't help to speed up
the crawl. We do our best to ensure we always have enough IPs to crawl sites at
this maximum speed.

Why is ProxyHub so slow from a browser?
---------------------------------------

This is common misconception. We often get this question from users who try
ProxyHub in their browsers, which (even though it works) it's not the way it's
intended to be used. Unlike typical proxy providers, ProxyHub is specifically
designed for crawling, by throttling requests speed to avoid users getting
banned. This throttling translates to a perception of ProxyHub being slow when
tried in a browser. Continue reading to understand why.

When you access a web page in a browser, you typically have to download many
resources to render it (images, CSS styles, javascript code, etc) an each
resource is a different request that needs to be performed against the site.
Compare this to crawling, where you typically only download the page HTML
source. Not only you need to perform many requests to render a single page, but
web browsers also limit the number of concurrent requests performed to any
single site. All this translates to ProxyHub looking slow when tried from a web
browser. But this "slowness" is actually a feature for the purpose that
ProxyHub is intended to be used.

Can I use my own User Agent?
----------------------------

You may have noticed that ProxyHub ignores the User-Agent you pass in the
requests and injects its own. This is intentional and it's part of the anti-bot
detection mechanism that ProxyHub enforces to keep crawlers from getting
banned. User agents are in fact kept the same within the same "crawl session"
(and do not blindly rotate in random fashion) to emulate better the behaviour
of a browser.

The other reason for overriding the user-agent is that we want websites to be
able to contact us if our crawls are causing them any trouble. ProxyHub is
designed to be polite and this should never happen, although there is always a
chance. Having been in the crawling business for a while, we know how important
is to identify your crawler properly and provide a way for website owners to
contact us. It's important to highlight that we will <b>never disclose any
customer information</b>, but we may have to occasionally stop a crawl if there
is a complain.


Are POST requests supported?
----------------------------

No. This is because ProxyHub may retry requests internally (when it detects
bans) and POST requests cannot be safely retried because they are not
idempotent. We may allow POSTs in future without retrying.


Can I use ProxyHub with other crawlers than Scrapy?
---------------------------------------------------

Absolutely. ProxyHub is not tied to Scrapy in any way. The ProxyHub service
provides a standard HTTP proxy interface that can be used with any crawler or
browser that supports proxies (most of them do). This page contains information
on how to use it with other clients, including the standard curl.</p>

What language is ProxyHub written on?
-------------------------------------

Erlang.

Do you plan to release ProxyHub as open source?
-----------------------------------------------

Not for the moment.



.. _scrapylib: https://github.com/scrapinghub/scrapylib
.. _HubProxyMiddleware: https://github.com/scrapinghub/scrapylib/blob/master/scrapylib/hubproxy.py
.. _the panel: http://panel.scrapinghub.com
.. _RetryMiddleware: http://doc.scrapy.org/en/latest/topics/downloader-middleware.html#module-scrapy.contrib.downloadermiddleware.retry
.. _Squid: http://www.squid-cache.org/
