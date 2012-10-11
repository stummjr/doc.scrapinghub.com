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


.. _scrapylib: https://github.com/scrapinghub/scrapylib
.. _HubProxyMiddleware: https://github.com/scrapinghub/scrapylib/blob/master/scrapylib/hubproxy.py
.. _ProxyHub page: http://www.scrapinghub.com/proxyhub.html
.. _the panel: http://panel.scrapinghub.com
.. _RetryMiddleware: http://doc.scrapy.org/en/latest/topics/downloader-middleware.html#module-scrapy.contrib.downloadermiddleware.retry
