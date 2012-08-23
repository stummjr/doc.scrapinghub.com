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

For more information, including pricing, check the `ProxyHub page`_.

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

    DOWNLOADER_MIDDLEWARES = {'scrapylib.hubproxy.HubProxyMiddleware': 1}

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

.. _scrapylib: https://github.com/scrapinghub/scrapylib
.. _HubProxyMiddleware: https://github.com/scrapinghub/scrapylib/blob/master/scrapylib/hubproxy.py
.. _ProxyHub page: http://www.scrapinghub.com/proxyhub.html
.. _the panel: http://panel.scrapinghub.com
