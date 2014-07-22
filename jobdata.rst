.. _jobdata:

==================
Job Data Retrieval
==================

Each job that runs on Scrapinghub scrapes an arbitrary number of items that are
automatically captured and stored in Scrapinghub built-in storage. These items
can be retrieved using the Items API.

Similarly, jobs generate logs for debugging which are also retrievable through
the :ref:`Logs API <logs-api>`.

Finally, there is one more entity that is automatically tracked on all jobs:
requests. These can be retrieved through the :ref:`Requests API <requests-api>`.

These APIs are all described below.

.. note:: Even though these APIs support writing, they are most often used for
   reading. The crawlers running on Scrapinghub cloud are the ones that write
   to these endpoints. However, both operations are documented here for
   completion.

.. _items-api:

Items API
=========

Reading items
-------------

Items written by a job can be retrieved by performing a GET::

    $ curl https://storage.scrapinghub.com/items/53/34/7

This retrieves all items for job ``53/34/7``, i.e project *57*, spider *34* and
run *7*. Items are returned in item key order, which is the same as the order in
which they were written.

Get the first item in that job::

    $ curl https://storage.scrapinghub.com/items/53/34/7/0

Get all items for spider ``34``::

    $ curl https://storage.scrapinghub.com/items/53/34

The spider can be omitted to get all items in the project. Items
are returned grouped by spider, then job and in the order in which
they were written within that job.

Pagination and meta parameters are supported, see :ref:`pagination` and
:ref:`metapar`.

A ``nodata`` parameter can be added to avoid fetching data. This can be useful,
for example, when combined with the ``meta`` parameter if the metadata is all
that is required::

    $ curl https://storage.scrapinghub.com/items/53/34/7?meta=_key&nodata=1
    {"_key":"1111111/1/1/0"}

When items are inserted, a count of the number of records and bytes stored is
maintained, along with counts of the field names used. This can be accessed via
the ``/stats`` path element. For example::

    $ curl https://storage.scrapinghub.com/items/53/34/7/stats
    {"counts":{"field1":9350,"field2":514},"totals":{"input_bytes":14390294,"input_values":10000}}

Field names beginning with **"_"** are considered hidden, and will only be returned if
an ``all`` parameter is present.

Currently, all items data is returned in JSON Lines format (JSON records, separated by
newlines) unless another format is specified in the *Accept* header. The
following formats are supported at present::

- application/x-jsonlines
- application/json
- text/csv

For example, to get items in JSON format using *curl*::

    $ curl -H "Accept: application/json" https://storage.scrapinghub.com/items/53/34/7/

An alternative to the *Accept* header is to pass in the URL the ``format`` parameter
who can take values of ``text``, ``csv``, ``json`` or ``jl``. It can be used
interchangeably with the *Accept* header::

    $ curl https://storage.scrapinghub.com/items/53/34/7?format=json

If CSV output format is used, a ``fields`` parameter must be specified to indicate the required fields and their order (a comma-separated list). An optional parameter is ``include_headers``, whose value can be ``1``, ``Y`` or ``y``, indicating whether to include the names of the fields in the top row of the CSV output::

    $ curl "https://storage.scrapinghub.com/items/53/34/7?format=csv&fields=id,name&include_headers=1"

``sep``, ``quote``, ``escape`` or ``lineend`` parameters may be used to control the separator character, quote character, escape character or line end string.

A single value of a field in a job can be retrieved as raw text (or HTML) data but specifying the field name in the path, for example::

    $ curl "https://storage.scrapinghub.com/items/53/34/7/fieldname"

Writing items
-------------

Items are added by POSTing data to a particular job, for example::

    $ curl -X POST -T items.jl https://storage.scrapinghub.com/items/53/34/7

The *Content-Range* header can be used to specify a start index. This is used in the client library to insert in batches.

For example::

    $ curl -X POST -T items.jl -H "ontent-range: items 500-/*" https://storage.scrapinghub.com/items/53/34/7

In all cases, the server will only return ``200 OK`` when the data has been committed securely.

There is no limit on the amount of data that can be posted, however, an ``HTTP 413`` response will be returned if any single item is over 1M.


.. _logs-api:

Logs API
========

Example log record::

    {"message": "Spider opened", "level": 20, "time": 1338987938007}

All log data is returned in plain text format (one row per log) unless another
format is specified in the *Accept* header. The following formats are
supported at present::

- application/x-jsonlines
- application/json
- text/plain
- text/csv

For example, to get logs in JSON Lines format using *curl*::

    $ curl -X GET -H "Accept: application/x-jsonlines" https://storage.scrapinghub.com/logs/1111111/1/1/

As is the case with job data, the *Accept* header can be substituted with the
``format`` parameter::

    $ curl -X GET https://storage.scrapinghub.com/logs/1111111/1/1?format=jl

CSV output accepts the same options as with items (``fields`` and
``include_headers`` parameters) with the exception that ``fields`` is now optional and
defaults to ``time,level,message`` (all headers).

Like items, logs are also added by POSTing data to a particular job, for example::

    $ curl -X POST -T log.jl https://storage.scrapinghub.com/logs/53/34/7

With the restriction that the records in the *log.jl* file must contain the
following fields:

* time *(number)* - the UNIX timestamp of the log message in *milliseconds* (must
  be integer)

* level *(number)* - the numeric value of the log level as defined in the Python
  logging library

* message *(string)* - the log message

Pagination and meta parameters are supported, see :ref:`pagination` and
:ref:`metapar`.


.. _requests-api:

Requests API
============

HTTP requests and responses can be tracked using the :ref:`requests-api` and can reference
item data.

Here is an example of reading data::

    $ curl https://storage.scrapinghub.com/requests/53/34/7
    {"parent":0,"duration":12,"status":200,"method":"GET","rs":1024,"url":"http://scrapy.org/","time":1351521736957}

Data can be read in JSON or JSON Lines format. Pagination and meta parameters
are supported, see :ref:`pagination` and :ref:`metapar`.

.. note:: ``method`` and ``time`` fields are not yet implemented.

Currently, the only stats traced are the count of items inserted and the bytes occupied::

    $ curl https://storage.scrapinghub.com/requests/53/34/7/stats
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

    $ curl -X POST -T requests.jl https://storage.scrapinghub.com/requests/53/34/7


Listing Jobs
============

It is often convenient to consume data from jobs once they finish running. The *JobQ API* can provide an ordered list of finished job keys, with the most recently finished first::

    $ curl https://storage.scrapinghub.com/jobq/53/list
    {"key":"53/7/81","ts":1397762393489}
    {"key":"53/7/80","ts":1395111612849}
    {"key":"53/7/78","ts":1393972804722}
    {"key":"53/7/77","ts":1393972734215}
    ...

A job key can be used with the items, logs or requests APIs to retrieve data, for example::

    $ curl https://storage.scrapinghub.com/items/53/7/81

This will get the items from the most recently finished job.

We recommend storing the key of the most recently finished job (``53/7/81`` in our example) along with the downloaded data. To update the dataset later, it is possible to list jobs, stopping at the previously downloaded job::

    $ curl https://storage.scrapinghub.com/jobq/53/list?stop=53/7/81
    {"key":"53/7/83","ts":1403610146780}
    {"key":"53/7/82","ts":1397827910849}

This retrieves all jobs that have finished since the specified job.

``ts`` is the timestamp at which the job was added to the finished queue. It is possible to return jobs finished between two timestamps::

    $ curl 'https://storage.scrapinghub.com/jobq/53/list?startts=1359774955431&endts=1359774955440'
    {"key":"53/6/7","ts":1359774955439}
    {"key":"53/3/3","ts":1359774955437}
    {"key":"53/9/1","ts":1359774955431}
