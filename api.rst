.. _api:

==========
API Basics
==========

All API calls must be authenticated with a valid Scrapinghub API key, using `HTTP basic auth`_ or an ``apikey`` URL argument.

* using `HTTP basic auth`_::

    $ curl -u APIKEY: http://storage.scrapinghub.com/foo

* using URL argumet::

    $ curl http://storage.scrapinghub.com/foo?apikey=APIKEY

You can get your API key at: http://dash.scrapinghub.com/account/

Using the HTTP API
==================

Resources (items, logs, etc.) are identified by the path element of the URL. It
has the format::

    /resource-type/projectid/spiderid/jobid/itemid

We call the ``projectid/spiderid/jobid/itemid`` path element the item 'key'.

For each API, we provide `curl`_ examples to illustrate the usage.

We highly recommend installing the `JSONView extension`_ (available for Firefox and Chrome) for visualizing API responses.


Available APIs
==============

This is a summary of the available APIs for reference. It is advised to read
their respective documentation chapter to understand how they work and how they
should be used (best practices, etc).

* :ref:`jobs-api`
* :ref:`items-api`
* :ref:`logs-api`
* :ref:`requests-api`
* :ref:`collections-api`
* :ref:`reports-api`
* :ref:`activity-api`
* :ref:`autoscraping-api`
* :ref:`eggs-api`
* :ref:`frontier-api`

HTTP Headers
============

Gzip compression is supported. A client can specify that gzip responses can be
handled using the "accept-encoding: gzip" request header. For requests, a a
"content-encoding: gzip" header must be present to signal the gzip content
encoding.

A `saveas` request parameter can be used to specify a filename for browser
downloads. For example, specifying `?saveas=foo.json` will cause a header of
`Content-Disposition: Attachment; filename=foo.json` to be returned.

.. _formats:

Data Formats
============

*JSON Lines* is a variation of JSON format, which is more convenient for streaming. It consists of one JSON object per line.

For example, this is JSON::

    [{"name": "hello", "price": "120"}, {"name": "world", "price": "540"}]

While this is the same data in JSON Lines format::

    {"name": "hello", "price": "120"}
    {"name": "world", "price": "540"}

To better support streaming with many popular json parsers, we provide jsonlines format by default, but JSON and CSV are also available.

.. _pagination:

Pagination and Random Access
============================

In many APIs, results can be paginated by supplying a `start` (or `startafter`)
and a `count` parameter. The start parameter is the item key.

.. note:: The following examples use the :ref:`items-api` but pagination is
   also supported by the :ref:`logs-api`, :ref:`requests-api` and
   :ref:`collections-api`

Get 10 items, starting from item 20::

    curl http://storage.scrapinghub.com/items/53/34/7?start=53/34/7/20&count=10

Get 10 items, starting from item 20 in job 7, this will read job 8, 9, etc. if
necessary::

    curl http://storage.scrapinghub.com/items/53/34?start=53/34/7/20&count=10

The `startafter` parameter starts from the next item following that key. This can
sometimes be useful, for example, if you pass the key of the last item read.

Specific items can be requested by providing an `index` parameter, which can be
repeated to request multiple values::

    curl http://storage.scrapinghub.com/items/53/34?index=3&index=10

A random sample of results can be fetched by setting the `start` parameter to the
keyword `random`. The `count` parameter specifies the size of the sample. This
is limited to up to 20 results and does not work across multiple jobs.

.. _metapar:

Meta parameters
===============

On many APIs, for JSON and JSON list types, the results can have extra data
added upon request, using the ``meta`` parameter, which may be repeated. The
following values are available:

=========       ===========
parameter       description
=========       ===========
_key            unique key for the element
_ts             timestamp in milliseconds when the item was added
=========       ===========

For example::

    $ curl http://storage.scrapinghub.com/items/53/34/7?meta=_key&meta=_ts
    {"_key":"1111111/1/1/0","_ts":1342078473363, ... }

Note that if the data contains fields with the same name as the requested meta
fields, they will both be output.

Python Library
==============

There is a Python client library for Scrapinghub API available here:

    https://github.com/scrapinghub/python-scrapinghub


.. _curl: http://curl.haxx.se/
.. _HTTP basic auth: http://en.wikipedia.org/wiki/Basic_access_authentication
.. _JSONView extension: http://benhollis.net/software/jsonview/
