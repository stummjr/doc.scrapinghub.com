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

Allows bot to crawl the target site more gently, by dynamically adjusting request concurrency and delay according to site lag and user
control parameters. For details see `Scrapy Autothrottle`_ documentation

This addon is loaded by default on any panel project. In short, the basic three settings that controls its behaviour are

* ``CONCURRENT_REQUESTS_PER_DOMAIN`` - Limits the maximum number of concurrent requests sent to the same host domain. Default value is 8.
* ``DOWNLOAD_DELAY`` - Limits the minimal download delay (in seconds) between each burst of requests. Default value is 0.
* ``AUTOTHROTTLE_ENABLED`` - Enables or disables the autothrottle addon. It is ``True`` (enabled) by default. 

**How to adjust these parameters?** There are not values that will work for any target server, and they also depends much on your needs.
The default values are in general a good starting point and most servers tolerate them. But you can still be blocked and you will need
to slow down the crawling rate, or may be you want more speed and so want to crawl faster, but with the increasing risk of being
blocked. 

In short, you can slow down the crawling rate from defaults by adjusting the maximum concurrency to 1, and arbitrarily
increasing the minimal download delay. Regarding the maximum effective crawling rate, in practice it will be limited to the target server response rate, but may try to
speed it up by arbitrarily increasing maximum concurrency (although this has not important effect in practice, as concurrency will be barely bigger than 2 for most sites). But of course, the maximum effective crawling rate will be limited to the target server response rate.

As autothrottle adjusts dynamically delay and concurrency depending on site lag, the parameters only sets limits, but does not
force them to have a given value. The minimal download delay value will not avoid the effective download delay take greater values
during crawling, and the maximal concurrency value will not avoid the effective concurrency take lower ones. If you really want to
avoid autothrottle to adjust effective parameters during crawling, and want to set them to fixed values, you need to disable the addon
by setting ``AUTOTHROTTLE_ENABLED`` to ``False``. Under such conditions, the settings ``CONCURRENT_REQUESTS_PER_DOMAIN`` and ``DOWNLOAD_DELAY`` are not limits, but
forced values. However, if you try to increase the crawling rate by proceeding in this way, you will also increase greatly the probability to be blocked by the target
site, so do that at your own risk. Also, Scrapinghub want to be polite with crawled sites.

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

.. _querycleaner:

Images
======

This addon downloads images from extracted image urls and stores them into an Amazon S3 storage. In order to enable, you must set the ``IMAGES_STORE`` setting,
and define two item fields:

* a field ``image_urls`` with type ``image``, which you will use to annotate image urls in the template. This will be the source field from which the addon will get the urls of the images to be downloaded.
* a field ``images``, where the addon will save important information about the stored image, including the s3 path relative to the ``IMAGES_STORE`` setting and the origin image url. The type of this field doesn't matter, as it is written by the pipeline, not by the AS extraction algorithm, but because of that, be sure that it is **NOT** flagged as required, otherwise you will not get extracted data, because you will not annotate this field in the templates.

Those field names are the defaults, but can be overriden with the settings ``IMAGES_URLS_FIELD`` and ``IMAGES_RESULT_FIELD``. The source and target fields defined by
these two settings does not need to be different ones. You can make both be the same. That avoids you to define an extra field in the item. The addon will just
overwrite the data extracted by AS, with the data it generates (which is a dict that already includes the origin url).

Settings:

* ``IMAGES_STORE`` - Provide the complete S3 base path (in format *s3://<bucket name>/<base path>/*) where to store images.
* ``IMAGES_MIN_WIDTH`` - Images with less than this width (in pixels) are ignored. Default value is 0.
* ``IMAGES_MIN_HEIGHT`` - Images with less than this height (in pixels) are ignored. Default value is 0.
* ``IMAGES_EXPIRES`` - When image is already in store, update it only when its age is older than this value (in days). Default value is 90.
* ``IMAGES_URLS_FIELD`` - Specify the item field from which the addon will read the image urls to download/store. Default value is ``image_urls``.
* ``IMAGES_RESULT_FIELD`` - Specify the item field where the addon will save the stored image information. Default value is ``images``.

You will also need to provide the standard ``AWS_ACCESS_KEY_ID`` and ``AWS_SECRET_ACCESS_KEY`` settings so the addon will be able to upload the images in your
s3 storage.

For more details, the Images Addon is actually based on the `Scrapy Images Pipeline`_.

Query Cleaner
=============

Query Cleaner addon allows to clean request url get query parameters at the output of the spider, according to patterns provided
by the user.

In order to enable, use at least one of the addon specific settings, ``QUERYCLEANER_REMOVE`` or ``QUERYCLEANER_KEEP``.
The first one specifies a pattern (regular expression) that must match any query parameter name in order to be removed from the url
(everyone else will be accepted), and the second one, a pattern that must match any query parameter name in order to be kept in the
url (everyone else will be removed). You can combine both if you want to keep some query parameters pattern, except some other one.
The remove pattern has precedence over the keep one.

Observe that you can specify a list of parameter names by using the | (OR) regex operator. For example, the pattern
``search|login|postid`` will match query parameters *search*, *login* and *postid*. This is by far the most common usage case.
Another typical usage case is the complete removal of all the url query, thus you will set ``QUERYCLEANER_REMOVE`` value to
``.*``

Supported settings:

* ``QUERYCLEANER_REMOVE``
* ``QUERYCLEANER_KEEP``

The addon is implicitly enabled when one of these settings is provided.

Lets suppose that the spider extracts urls like::

    http://www.example.com/product.php?pid=135&cid=12&ttda=12

and we want to leave only the parameter ``pid``. We can specify this in two ways, either using ``QUERYCLEANER_REMOVE`` or
``QUERYCLEANER_KEEP``. In the first case, the pattern used would be ``cid|ttda``. In the second case, ``pid``. The best
solution depends on particular case, that is, how the query filters will affect any other url that the spider is expected to extract.

.. _Scrapy: https://github.com/scrapy/scrapy
.. _DeltaFetch code:  https://github.com/scrapinghub/scrapylib/blob/master/scrapylib/deltafetch.py
.. _`Scrapy Autothrottle`: https://scrapy.readthedocs.org/en/latest/topics/autothrottle.html
.. _`Scrapy Images Pipeline`: http://doc.scrapy.org/en/latest/topics/images.html
