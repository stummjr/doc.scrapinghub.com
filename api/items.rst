.. _api-items:

=========
Items API
=========

.. note:: Even though these APIs support writing, they are most often used for reading. The crawlers running on Scrapinghub cloud are the ones that write to these endpoints. However, both operations are documented here for completion.
 
Endpoints:

<toc>

Reading
-------

Items written by a job can be retrieved by performing a GET::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7

This retrieves all items for job ``53/34/7``, i.e project *53*, spider *34* and
run *7*. Items are returned in item key order, which is the same as the order in
which they were written.

Get the first item in that job::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7/0

Get all items for spider ``34``::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34

The spider can be omitted to get all items in the project. Items
are returned grouped by spider, then job and in the order in which
they were written within that job.

Pagination and meta parameters are supported, see :ref:`pagination` and
:ref:`metapar`.

A ``nodata`` parameter can be added to avoid fetching data. This can be useful,
for example, when combined with the ``meta`` parameter if the metadata is all that is required::

    $ curl -u APIKEY: "https://storage.scrapinghub.com/items/53/34/7?meta=_key&nodata=1"
    {"_key":"1111111/1/1/0"}

When items are inserted, a count of the number of records and bytes stored is
maintained, along with counts of the field names used. This can be accessed via
the ``/stats`` path element. For example::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7/stats
    {"counts":{"field1":9350,"field2":514},"totals":{"input_bytes":14390294,"input_values":10000}}

Field names beginning with **"_"** are considered hidden, and will only be returned if
an ``all`` parameter is present.

Currently, all items data is returned in JSON Lines format (JSON records, separated by
newlines) unless another format is specified in the *Accept* header. The
following formats are supported at present::

- application/x-jsonlines
- application/json
- application/xml
- text/csv

For example, to get items in JSON format using *curl*::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7/ -H "Accept: application/json"

An alternative to the *Accept* header is to pass in the URL the ``format`` parameter
who can take values of ``text``, ``csv``, ``xml``, ``json`` or ``jl``. It can be used
interchangeably with the *Accept* header::

    $ curl -u APIKEY: "https://storage.scrapinghub.com/items/53/34/7?format=json"

`XML-RPC data types`_ is used for XML format output.

If CSV output format is used, a ``fields`` parameter must be specified to indicate the required fields and their order (a comma-separated list). An optional parameter is ``include_headers``, whose value can be ``1``, ``Y`` or ``y``, indicating whether to include the names of the fields in the top row of the CSV output::

    $ curl -u APIKEY: "https://storage.scrapinghub.com/items/53/34/7?format=csv&fields=id,name&include_headers=1"

``sep``, ``quote``, ``escape`` or ``lineend`` parameters may be used to control the separator character, quote character, escape character or line end string.

A single value of a field in a job can be retrieved as raw text (or HTML) data but specifying the field name in the path, for example::

    $ curl -u APIKEY: "https://storage.scrapinghub.com/items/53/34/7/fieldname"

Writing
-------

Items are added by POSTing data to a particular job, for example::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7 -X POST -T items.jl

The *Content-Range* header can be used to specify a start index. This is used in the client library to insert in batches.

For example::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7 -X POST -T items.jl -H "content-range: items 500-/*"

In all cases, the server will only return ``200 OK`` when the data has been committed securely.

There is no limit on the amount of data that can be posted, however, an ``HTTP 413`` response will be returned if any single item is over 1M.

.. _XML-RPC data types: http://en.wikipedia.org/wiki/XML-RPC#Data_types
