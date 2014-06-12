===========
Collections
===========

Scrapinghub Collections provide a way to store arbitrary number of records indexed by
a key. They're often used by Scrapinghub projects as a single place to write
information from multiple scraping jobs.

The Collections API is described below.

.. _collections-api:

Collections API
---------------

The Collections API allows storing arbitrary objects in named sets. For example::

    $ curl -X POST -d '{"_key": "foo", "value": "bar"}' \
        http://storage.scrapinghub.com/collections/78/s/my_collection

Posts an object to the 'my_collection' collection. A string _key field must be specified and
multiple objects may be posted when separated by newlines.

In the above example, the '/s/' in the path represents the collection type. The following
collection types are available:

====    =====================   ================================================================
path    name
====    =====================   ================================================================
s       store                   Basic set store
cs      cached store            Items expire after a month
vs      versioned store         Up to 3 copies of each item will be retained
vcs     versioned cache store   Multiple copies are retained, and each one expires after a month
====    =====================   ================================================================

Individual items can be read directly (assuming the key is a string)::

    $ curl http://storage.scrapinghub.com/collections/78/s/my_collection/foo
    {"value":"bar"}

Or they can be retrieved using a key parameter, which can be present multiple times::

    $ curl http://storage.scrapinghub.com/collections/78/s/my_collection?key=foo1&key=foo2
    {"value":"bar1"}
    {"value":"bar2"}

and the value of an item field can also be retrieved (text & html mime types supported)::

    $ curl http://storage.scrapinghub.com/collections/78/s/my_collection/foo/value
    bar

Filtering, pagination and meta parameters work the same as in the items API.
However, there is an additional filter that allows efficient filtering on key
prefixes::

    $ curl http://storage.scrapinghub.com/collections/78/s/my_collection?prefix=f
    {"value":"bar"}

Prefix filters should be used where possible as they use indexes, unlike other filters.

Individual objects can also be deleted::

    $ curl -X DELETE http://storage.scrapinghub.com/collections/78/s/my_collection/foo

Or multiple items at once by posting the key as JSON to the '/deleted' endpoint as follows::

    $  curl -X POST -d '"foo"' http://storage.scrapinghub.com/collections/78/s/my_collection/deleted


To delete many objects based on a query, the delete must be performed in
batches in order to avoid long queries and timeouts. Each query returns
an object containing a the number of delted records and a start parameter to
continue the deletion. For example::

    $ curl -X DELETE http://storage.scrapinghub.com/collections/78/s/my_large_collection
    {"nextstart":"SOMEKEY","scanned":254658,"deleted":254658}

Which can be continued::

    $ curl -X DELETE http://storage.scrapinghub.com/collections/78/s/my_large_collection&start=SOMEKEY
    {"nextstart":"SOMEKEY","scanned":228762,"deleted":228762}

and the final query will have no 'nextstart' field in the response::

    $ curl -X DELETE http://storage.scrapinghub.com/collections/78/s/my_large_collection&start=SOMEKEY
    {"scanned":10678,"deleted":10678}

The total number of records deleted is the sum of those deleted in each request. Filters
can also be used with this endpoint and prefix filters should be used where possible.

The count endpoint counts the number of matching records and works in a similar way::

    $ curl http://storage.scrapinghub.com/collections/78/s/my_large_collection/count?prefix=P
    {"nextstart":"PXXX","scanned":2465,"count":2465}
    $ curl http://storage.scrapinghub.com/collections/78/s/my_large_collection/count?prefix=P&start=PXXX
    {"scanned":7634,"count":7634}

The scanned parameter returns the number of records scanned. It may be higher than the
deleted or counted number if a filter other than prefix filter is used. By default, the number
of scanned records is limited to 500000 in a single request. It can be lowered by setting a
`maxscan` parameter in the request.

There is a 'scan' endpoint for filtering in batches. This is more appropriate if the filter may
take a while to run and there is a risk of timeouts. The result is always JSON and is returned
in a 'values' field:

    $ curl 'http://storage.scrapinghub.com/collections/78/s/my_collection/scan?prefix=f&maxscan=1'
    {"values":[{"value":"bar1"}], "nextstart":"foo2"}

