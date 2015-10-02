.. _api-requests:

============
Requests API
============

HTTP requests and responses can be tracked using the :ref:`requests-api` and can reference
item data.

Here is an example of reading data::

    $ curl -u APIKEY: https://storage.scrapinghub.com/requests/53/34/7
    {"parent":0,"duration":12,"status":200,"method":"GET","rs":1024,"url":"http://scrapy.org/","time":1351521736957}

Data can be read in JSON or JSON Lines format. Pagination and meta parameters
are supported, see :ref:`pagination` and :ref:`metapar`.

.. note:: ``method`` and ``time`` fields are not yet implemented.

Currently, the only stats traced are the count of items inserted and the bytes occupied::

    $ curl -u APIKEY: https://storage.scrapinghub.com/requests/53/34/7/stats
    {"totals":{"input_bytes":64,"input_values":2}}

The following fields are supported:

=========   ========        ===================================================
Field       Required        Description
=========   ========        ===================================================
parent      no              The index of the parent request (if unspecified,
                            the request is a ``start_url``)
duration    yes             Request duration in milliseconds
status      yes             HTTP status code of the response
method      no              HTTP method used (if unspecified, GET is used as the
                            default)
rs          yes             Response size in bytes
url         yes             Request URL
fp          no              Request fingerprint (string)
=========   ========        ===================================================

Data is inserted by POSTing JSON lists::

    $ curl -u APIKEY: https://storage.scrapinghub.com/requests/53/34/7 -X POST -T requests.jl


