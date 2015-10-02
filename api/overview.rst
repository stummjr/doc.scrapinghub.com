.. _api-oveview:

===============
Scrapinghub API
===============

Scrapinghub's API provides an interface for interacting with your spiders, jobs and scraped data.

Example
=======

Want to schedule a spider? Simple::

    curl -u <API key>: https://dash.scrapinghub.com/api/schedule.json -d project=<project id> -d spider=<spider>

Where `<API key>` is your API key, `<project id>` is the spider's project ID, and `<spider>` is the name of the spider you want to schedule.

Authentication
==============

Authenticate using your `API key <https://dash.scrapinghub.com/account/apikey>`_. 

There are two ways to authenticate.

HTTP Basic::

    curl -u <API key>: https://storage.scrapinghub.com/foo

URL Parameter::

    curl https://storage.scrapinghub.com/foo?apikey=<API key>

Endpoints
=========

* :ref:`api-activity`
* :ref:`api-collections`
* :ref:`api-comments`
* :ref:`api-eggs`
* :ref:`api-frontier`
* :ref:`api-items`
* :ref:`api-jobs`
* :ref:`api-logs`
* :ref:`api-reports`

Libraries
=========

There are two libraries available for interacting with Scrapinghub.

python-scrapinghub
------------------

https://github.com/scrapinghub/python-scrapinghub

python-hubstorage
-----------------

https://github.com/scrapinghub/python-hubstorage

Pagination
==========

You can paginate the results for the majority of the APIs using a number of parameters.

`start` parameter is the offset number of elements you want to start at. Setting the `start` parameter will return a random sample of results.
`count` is the number of results per page.

The `index` parameter can be used to retrieve a specific value. You can provide multiple values per request to retrieve several specific records.

Headers
=======

*gzip* compression is supported. A client can specify that *gzip* responses can be handled using the ``accept-encoding: gzip`` request header. ``content-encoding: gzip`` header must be present in the response to signal the *gzip* content encoding.

A ``saveas`` request parameter can be used to specify a filename for browser downloads. For example, specifying ``?saveas=foo.json`` will cause a header of ``Content-Disposition: Attachment; filename=foo.json`` to be returned.

Meta parameters
===============

The `meta` parameter instructs the endpoint to return metadata for the record in addition to its core data.

The following values are available:

=========   ===========
Parameter   Description
=========   ===========
_key        Unique key for the element.
_ts         Timestamp in milliseconds for when the item was added.
=========   ===========


Example::

    $ curl https://storage.scrapinghub.com/items/53/34/7?meta=_key&meta=_ts
    {"_key":"1111111/1/1/0","_ts":1342078473363, ... }

.. note:: If the data contains fields with the same name as the requested fields, they will both appear in the result.


