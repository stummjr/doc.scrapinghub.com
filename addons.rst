.. _addons:

======
Addons
======

Addons allow to extend bot capabilities and configure them easily from the panel. Each addon provides a particular feature.
There are two flavours of addons: builtin addons and user addons. Builtin addons are always enabled and cannot be disabled by users.

There are some generic addons, and there are specific addons for facilitating some common tasks in autoscraping spiders in order to
fastly apply without need to write and deploy code in the project. And many addons are just a wrapper of some standard `Scrapy`_ or
`Scrapylib`_ components.

The effective enabling of an addon consist at least on two steps: adding the addon to the project, and enabling
by means of a setting, either at project or spider level, thus allowing a fine tunning. Further steps may include the provision of
required and not required settings for configuring the addon behaviour.

.. toctree::

Autothrottle
____________

Allows bot to crawl the target site more gently, by adjusting request concurrency and delay according to site lag and user control parameters.
For details see `Scrapy Autothrottle`_ documentation

DeltaFetch
__________

The purpose of this addon is to ignore requests to pages containing items seen in previous crawls of the same spider,
thus producing a "delta crawl" containing only new items. For algorithm details see deltafetch code in `Scrapylib`_ repository.

The only setting directly associated with this addon is

* **DELTAFETCH_ENABLED** - If ``True``, enables addon.

However, this addon depends also on `DotScrapy Persistence`_ addon (and thus, on its specific settings), so be sure to also enable it
and provided its required settings for proper working.

DotScrapy Persistence
_____________________

Allows the crawler to access a persistent storage and share data among different runs of a spider, by syncing the local project
data dir into s3 storage.

In order to access the local project data dir from a spider or project component code, the following code must be used::

    from scrapy.utils.project import data_path

    (...)

    dir = data_path()

And you can also save/read your data into proper subfolders::

    subdir = data_path(<subdir_name>)

DotScrapy Persistence just syncs all the content of the local project data dir into our scrapinghub cloud s3 storage. When the spider is
opened, it brings the remote data from the storage. When the scrapy engine is stopped, it writes back to it.

In order to use this addon, you must ask scrapinghub staff to provide AWS key pairs that will enable your project to access our s3
storage.

Settings list:

* **DOTSCRAPYPERSISTENCE_ENABLED** - If ``True``, enables addon.
* **AWS_ACCESS_KEY_ID**
* **AWS_SECRET_ACCESS_KEY**

ProxyHub
--------

 ProxyHub provides an HTTP proxy, with a pool of rotating IPs, designed specifically for scraping purposes. For details see
 `ProxyHub documentation`_.

Query Cleaner
_____________

**(Will be available on next release)**

Query Cleaner addon allows to clean request url get query parameters at the output of the spider, according to patterns provided
by the user.

In order to enable, use at least one of the addon specific settings, **QUERYCLEANER_DENIED_PATTERN** or **QUERYCLEANER_ALLOWED_PATTERN**.
The first one specifies a pattern (regular expression) that must match any query parameter name in order to be removed from the url
(everyone else will be accepted), and the second one, a pattern that must match any query parameter name in order to be included in the
url (everyone else will be removed). You can combine both if you want to allow some query parameters pattern, except some other one.
The denied pattern has precedence over the allowed one.

Observe that you can specify a list of parameter names by using the | (OR) regex operator. For example, the pattern
``search|login|postid`` will match query parameters *search*, *login* and *postid*. This is by far the most common usage case.
Another typical usage case is the complete removal of all the url query, thus you will set **QUERYCLEANER_DENIED_PATTERN** value to
``.*``

Settings list:

* **QUERYCLEANER_DENIED_PATTERN**
* **QUERYCLEANER_ALLOWED_PATTERN**

The addon is implicitly enabled when one of these settings is provided.

Lets suppose that the spider extracts urls like::

    http://www.example.com/product.php?pid=135&cid=12&ttda=12

and we want to leave only the parameter ``pid``. We can specify this in two ways, either using **QUERYCLEANER_DENIED_PATTERN** or
**QUERYCLEANER_ALLOWED_PATTERN**. In the first case, the pattern used would be ``cid|ttda``. In the second case, ``pid``. The best
solution depends on particular conditions.

.. _Scrapy: https://github.com/scrapy/scrapy
.. _Scrapylib: https://github.com/scrapinghub/scrapylib/
.. _`Scrapy Autothrottle`: https://scrapy.readthedocs.org/en/latest/topics/autothrottle.html
.. _`ProxyHub documentation`: http://help.scrapinghub.com/proxyhub.html

