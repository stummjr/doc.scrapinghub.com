=======
Old API
=======

.. warning:: This API is supported but deprecated. Please use the :ref:`New API <api>` instead.

Old Items API
=============

items.json
----------

Retrieves scraped items of a job.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``job`` or ``spider`` *(required)* - the job or spider to retrieve items from. If you specify a job, the items scraped on that job will be returned. If you specify a spider, the items scraped on the *last finished job* of that spider will be returned.
  * ``count`` and ``offset`` - see :ref:`pagination`

*Examples:*

To retrieve items scraped by job ``123/1/4``::

    curl -L -u APIKEY: "https://dash.scrapinghub.com/api/items.json?project=123&job=123/1/4"

.. warning:: This only returns the first 100 items. See :ref:`pagination`. If you want to return all items in one stream, you can use `items.jl`_.

To retrieve items scraped by the *last finished job* of the spider ``myspider``::

    curl -L -u APIKEY: "https://dash.scrapinghub.com/api/items.json?project=123&spider=myspider"

To retrieve the latest 20 items of job ``123/1/4`` *(this works even while the job is running)*::

    curl -L -u APIKEY: "https://dash.scrapinghub.com/api/items.json?project=123&job=123/1/4&count=-20"

items.jl
--------

Similar to `items.json`_ but returns the items in jsonlines format, which allows to retrieve all items without having to paginate the results.

*Example:*

To retrieve all items scraped by job ``123/1/4``::

    curl -L -u APIKEY: "https://dash.scrapinghub.com/api/items.jl?project=123&job=123/1/4"

items.csv
---------

Similar in usage to `items.json`_ and `items.jl`_, but returns items in CSV format and requires two extra parameters: ``fields`` and ``include_headers``.

* Extra Parameters:

  * ``fields`` *(required)* - a comma separated list of item fields to include in the exported CSV file
  * ``include_headers`` *(required)* - ``1`` if the exported CSV file should contain fields' headers in the first row, ``0`` otherwise

*Examples:*

To retrieve all items scraped by job ``123/1/4``, this time in CSV format, and dump *name*, *url* and *price* fields, excluding headers::

    curl -L -u APIKEY: "https://dash.scrapinghub.com/api/items.csv?project=123&job=123/1/4&include_headers=0&fields=name,url,price"


Old Logs API
============

log.txt
-------

Retrieves the log of a job.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``job`` *(required)* - the job to retrieve items from
  * ``level`` - the minimum log level to return, e.g. DEBUG, WARNING, ERROR, etc. (if not given, returns all log levels)
  * ``count`` and ``offset`` - see :ref:`pagination`

*Example:*

To retrieve the log of job ``123/1/4`` in plain text format::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/log.txt?project=123&job=123/1/4"

log.json
--------

Similar to `log.txt`_ but returns the log entries as a list of JSON objects containing the properties ``logLevel``, ``message`` and ``time``.

log.jl
--------

Similar to `log.json`_ but returns the log entries in jsonlines format.


Old Spiders API
===============

spiders/list.json
-----------------

Retrieves information about spiders.

* Supported Request Methods: ``GET``

* Filtering parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``spider`` - a spider name

*Examples:*

To retrieve all spiders in project ``123``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/spiders/list.json?project=123"

To retrieve spiders ``aspider`` and ``bspider`` in project ``123``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/spiders/list.json?project=123&spider=aspider&spider=bspider"

.. _pagination:

Paginating API Results
======================

All API calls that return multiple items in JSON format are limited to return 100 items per call, at most. These API calls support two parameters that can be used for paginating the results. Those are:

* ``count`` - limit the number of results to return: negative counts are supported as well making it possible to return the *latest* entries, instead of the first ones
* ``offset`` - a number of results to skip from the beginning

