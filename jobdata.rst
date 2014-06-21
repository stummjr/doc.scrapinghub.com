.. _jobdata:

==================
Job Data Retrieval
==================

Each job that runs on Scrapinghub scrapes an arbitrary number of items that are
automatically captured and stored in Scrapinghub built-in storage. These items
can be retrieved using the Items API.

Similarly, jobs generate logs for debugging which are also retrievable through
the Logs API.

Finally, there is one more entitiy that is automatically tracked on all jobs:
requests. These can be retrieved through the Requests API.

These APIs are all described below.

.. note:: Even though these APIs support writing they are most often used for
   reading. The crawlers running on Scrapinghub cloud are the ones that write
   to these endpoints. However, both operations are documented here for
   completion.

.. _items-api:

Items API
=========

Reading items
-------------

The data can be retrieved by performing a GET::

    $ curl https://storage.scrapinghub.com/items/53/34/7

Get the first item in that job::

    $ curl https://storage.scrapinghub.com/items/53/34/7/0

Get all items for spider 34::

    $ curl https://storage.scrapinghub.com/items/53/34

The spider can be omitted to get all items in the project and if the project
is omitted then all items in the database are returned.

Pagination and meta parameters are supported. See :ref:`pagination` and
:ref:`metapar`.

A `nodata` parameter can be added to avoid fetching data. This can be useful,
for example, when combined with the `meta` parameter if the metadata is all
that is required::

    $ curl https://storage.scrapinghub.com/items/53/34/7?meta=_key&nodata=1
    {"_key":"1111111/1/1/0"}

When items are inserted, a count of the number of records and bytes stored is
maintained, along with counts of the field names used. This can be accessed via
the /stats path element. For example::

    $ curl https://storage.scrapinghub.com/items/53/34/7/stats
    {"counts":{"field1":9350,"field2":514},"totals":{"input_bytes":14390294,"input_values":10000}}

Field names beginning with '_' are considered hidden, and will only be returned if
an `all` parameter is present.

Currently, all items data is return in JSONLines format (json records, separated by
newlines) unless another format is specified in the Accept Header, currently the
following formats are supported::

- application/x-jsonlines
- application/json
- text/csv

for example, to get items in json format using curl::

    $ curl -H "Accept: application/json" https://storage.scrapinghub.com/items/53/34/7/

An alternative to the Accept Header is to pass in the url the format parameter
who can take values of `text`, `csv`, `json` or `jl`. It can be used
interchangeably with the Accept Header::

    $ curl https://storage.scrapinghub.com/items/53/34/7?format=json

If csv output format is used, a `fields` parameter must be specified to
indicate the required fields and their order (comma-separated list). An
optional parameter is `include_headers` whose value can be `1`, `Y` or `y` and
indicates whether to include the names of the fields on the top row of the csv
output::

    $ curl "https://storage.scrapinghub.com/items/53/34/7?format=csv&fields=id,name&include_headers=1"

'sep', 'quote', 'escape' or 'lineend' parameters may be used to control the separator character,
quote character, escape character or line end string.


A single value of a field in a job can be retrieved as raw text (or html) data but specifying the field name in the path, for example::

    $ curl "https://storage.scrapinghub.com/items/53/34/7/fieldname"

Writing items
-------------

Items are added by POSTing data to a particular JOB, for example::

    $ curl -X POST -T items.jl https://storage.scrapinghub.com/items/53/34/7

The content-range header can be used to specify a start index. This is used in
the client library to insert in batches.

For example::

    $ curl -X POST -T items.jl -H "content-range: items 500-/*" https://storage.scrapinghub.com/items/53/34/7

In all cases, the server will only return 200 OK when the data has been
committed securely.

There is no limit on the amount of data that can be posted, however, an HTTP
413 response will be returned if any single item is over 1M.

.. _logs-api:

Logs API
========

Example log record::

    {"message": "Spider opened", "level": 20, "time": 1338987938007}

All log data is return in plain text format (one row per log) unless another
format is specified in the Accept Header, currently the following formats are
supported::

- application/x-jsonlines
- application/json
- text/plain
- text/csv

for example, to get logs in jsonlines format using curl::

    $ curl -X GET -H "Accept: application/x-jsonlines" https://storage.scrapinghub.com/logs/1111111/1/1/

As is the case with jobdata, the Accept Header can be substituted with the
`format` parameter::

    $ curl -X GET https://storage.scrapinghub.com/logs/1111111/1/1?format=jl

Csv output accepts the same options as with items (`fields` and
`include_headers` params) with the exception that `fields` is now optional and
defaults to "time,level,message" (all headers).

Like items, logs are also added by POSTing data to a particular JOB, for example::

    $ curl -X POST -T log.jl https://storage.scrapinghub.com/logs/53/34/7

With the restriction that the records in the log.jl file must contain the
following fields:

* time (number) - the unix timestamp of the log message in *milliseconds* (must
  be integer)

* level (number) - the numeric value of the log level as defined in the python
  logging library

* message (string) - the log message

Pagination and meta parameters are supported. See :ref:`pagination` and
:ref:`metapar`.

.. _requests-api:

Requests API
============

HTTP requests and responses can be tracked using the requests API and can reference
item data.

Here is an example of reading data::

    $ curl https://storage.scrapinghub.com/requests/53/34/7
    {"parent":0,"duration":12,"status":200,"method":"GET","rs":1024,"url":"http://scrapy.org/","time":1351521736957}

Data can be read in json, or jsonlines format. Pagination and meta parameters
are supported. See :ref:`pagination` and :ref:`metapar`.

.. note:: ``method`` and ``time`` fields are not yet implemented.

Currently, the only stats traced are the count of items inserted and the bytes occupied::

    $ curl https://storage.scrapinghub.com/requests/53/34/7/stats
    {"totals":{"input_bytes":64,"input_values":2}}

The following fields are supported:

=========   ========        ===================================================
Field       Required        Description
=========   ========        ===================================================
parent      no              The index of the parent request. If unspecified,
                            the request is a start_url
duration    yes             Request duration in milliseconds
status      yes             HTTP status code of the response
method      no              HTTP method used. If unspecified, GET is used as the
                            default.
rs          yes             Response size in bytes
url         yes             Request URL
fp          no              Request fingerprint (string)
=========   ========        ===================================================

Data is inserted by posting json lists::

    $ curl -X POST -T requests.jl https://storage.scrapinghub.com/requests/53/34/7

