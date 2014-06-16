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

Data formats
============

*JSON Lines* is a variation of JSON format, which is more convenient for streaming. It consists of one JSON object per line.

For example, this is JSON::

    [{"name": "hello", "price": "120"}, {"name": "world", "price": "540"}]

While this is the same data in JSON Lines format::

    {"name": "hello", "price": "120"}
    {"name": "world", "price": "540"}

To better support streaming with many popular json parsers, we provide jsonlines format by default, but JSON and CSV are also available.

Pagination
==========

Most API calls support the following arguments for pagination:

* ``count`` - limit the number of results to return: negative counts are supported as well making it possible to return the *latest* entries, instead of the first ones
* ``offset`` - a number of results to skip from the beginning

Python Library
==============

There is a Python client library for Scrapinghub API available here:

    https://github.com/scrapinghub/python-scrapinghub


.. _curl: http://curl.haxx.se/
.. _HTTP basic auth: http://en.wikipedia.org/wiki/Basic_access_authentication
.. _JSONView extension: http://benhollis.net/software/jsonview/
