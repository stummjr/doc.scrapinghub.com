.. _addons:

======
Addons
======

Addons allow to extend bot capabilities and configure them easily from the panel. Each addon provides a particular feature.
There are two flavours of addons: builtin addons and user addons. Builtin addons are always enabled and cannot be disabled by users.

Addons are very popular among Autoscraping users, because it allows to enable additional functionality without writing any code. Scrapy Cloud users often tend to write the extensions themselves. In fact, addons are no more than a fancy UI to configure `Scrapy`_ extensions.

To enable an addon, you have to:

1. add it to your project, by going to Settings -> Addons in the Scrapinghub panel

2. enable it. Some addons are enabled automatically when added, while others
   require to configure a setting (like ADDON_ENABLED). It should be clear from
   the addon page (in the panel) which case it is.

You can also enable addons per spider, instead of project wide, by going to the
spider page in the panel, and adding the ADDON_ENABLED setting in the
"Settings" section of that spider.

Autothrottle
============

Allows bot to crawl the target site more gently, by adjusting request concurrency and delay according to site lag and user control parameters.
For details see `Scrapy Autothrottle`_ documentation

DeltaFetch
==========

The purpose of this addon is to ignore requests to pages containing items seen
in previous crawls of the same spider, thus producing a "delta crawl"
containing only new items. For more details on the algorithm, you can check the
`DeltaFetch code`_.

There is a single setting for controlling this addon:

* ``DELTAFETCH_ENABLED`` - set it to enable the DeltaFetch addon (either project-wide or per spider)

Note that this addon depends on the `DotScrapy Persistence`_ addon, so make
sure to enable it before.

DotScrapy Persistence
=====================

This addon keeps the content of the ``.scrapy`` directory in a persistent
store, which is loaded when the spider starts and stored when the spider
finishes.

This allows spiders to share data among different runs, keeping state or any
kind of any kind of data that needs to be persisted among runs.

The ``.scrapy`` directory is well known in Scrapy and a few extensions use it
to keep state among runs. The canonical way to work with the ``.scrapy``
directory is by calling the ``scrapy.utils.project.data_path`` function, as
illustrated in the following example::

    from scrapy.utils.project import data_path

    mydata_path = data_path()

    # ... use mydata_path to store or read data which will be persisted among runs ...

Supported settings:

* ``DOTSCRAPY_ENABLED`` - set it to enable the DotScrapy addon (either project-wide or per spider)

ProxyHub
========

 ProxyHub provides an HTTP proxy, with a pool of rotating IPs, designed
 specifically for crawling purposes. For details see the `ProxyHub documentation`_.

Query Cleaner
=============

**(Will be available on next release)**

Query Cleaner addon allows to clean request url get query parameters at the output of the spider, according to patterns provided
by the user.

In order to enable, use at least one of the addon specific settings, ``QUERYCLEANER_DENIED_PATTERN`` or ``QUERYCLEANER_ALLOWED_PATTERN``.
The first one specifies a pattern (regular expression) that must match any query parameter name in order to be removed from the url
(everyone else will be accepted), and the second one, a pattern that must match any query parameter name in order to be included in the
url (everyone else will be removed). You can combine both if you want to allow some query parameters pattern, except some other one.
The denied pattern has precedence over the allowed one.

Observe that you can specify a list of parameter names by using the | (OR) regex operator. For example, the pattern
``search|login|postid`` will match query parameters *search*, *login* and *postid*. This is by far the most common usage case.
Another typical usage case is the complete removal of all the url query, thus you will set ``QUERYCLEANER_DENIED_PATTERN`` value to
``.*``

Supported settings:

* ``QUERYCLEANER_DENIED_PATTERN``
* ``QUERYCLEANER_ALLOWED_PATTERN``

The addon is implicitly enabled when one of these settings is provided.

Lets suppose that the spider extracts urls like::

    http://www.example.com/product.php?pid=135&cid=12&ttda=12

and we want to leave only the parameter ``pid``. We can specify this in two ways, either using ``QUERYCLEANER_DENIED_PATTERN`` or
``QUERYCLEANER_ALLOWED_PATTERN``. In the first case, the pattern used would be ``cid|ttda``. In the second case, ``pid``. The best
solution depends on particular conditions.

.. _Scrapy: https://github.com/scrapy/scrapy
.. _DeltaFetch code:  https://github.com/scrapinghub/scrapylib/blob/master/scrapylib/deltafetch.py
.. _`Scrapy Autothrottle`: https://scrapy.readthedocs.org/en/latest/topics/autothrottle.html
.. _`ProxyHub documentation`: http://help.scrapinghub.com/proxyhub.html

