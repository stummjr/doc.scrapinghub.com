.. _api:

==========
API Basics
==========

All API calls must be authenticated with a valid Scrapinghub API key, using
`HTTP basic auth`_ or an ``apikey`` URL argument.

We provide `curl`_ examples to illustrate how to use the API.

You can get your API at: http://dash.scrapinghub.com/account/

We highly recommend installing the `JSONView extension`_ (available for Firefox and Chrome) for visualizing API responses.

Using the HTTP API
==================

Requests must be authenticated with a valid Scrapinghub API key using HTTP
basic auth, for example::

    $ curl -u APIKEY: http://storage.scrapinghub.com/...

You can get your API at: http://dash.scrapinghub.com/account/

Resources (items, logs, etc.) are identified by the path element of the URL. It
has the format::

    /resource-type/projectid/spiderid/jobid/itemid

We call the ``projectid/spiderid/jobid/itemid`` path element the item 'key'.

Passing the API key in a ``apikey`` URL argument is also supported.

HTTP Headers
============

Gzip compression is supported. A client can specify that gzip responses can be
handled using the "accept-encoding: gzip" request header. For requests, a a
"content-encoding: gzip" header must be present to signal the gzip content
encoding.

A `saveas` request parameter can be used to specify a filename for browser
downloads. For example, specifying `?saveas=foo.json` will cause a header of
`Content-Disposition: Attachment; filename=foo.json` to be returned.

Available APIs
==============

This is a summary of the available APIs for reference. It is advised to read
their respective documentation section to understand how they work and how they
should be used (best practices, etc).

* `jobs-api`
* `items-api`
* `logs-api`
* `requests-api`
* `collections-api`
* `reports-api`
* `activity-api`
* `autoscraping-api`
* `eggs-api`
* `frontier-api`


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
.. _reStructuredText: http://en.wikipedia.org/wiki/ReStructuredText
