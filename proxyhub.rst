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

For more information, including pricing, check the `ProxyHub`_ page.

Usage
=====

ProxyHub provides a standard HTTP proxy interface so you can use it with any
software that supports them.

Here is an example using ProxyHub with curl to download
http://scrapinghub.com::

    curl -x proxy.scrapinghub.com:8010 -U USER:PASS http://scrapinghub.com

Where ``USER`` and ``PASS`` should be replaced by the credentials you got when
you signed up for the ProxyHub service.

.. _ProxyHub: http://www.scrapinghub.com/proxyhub.html
