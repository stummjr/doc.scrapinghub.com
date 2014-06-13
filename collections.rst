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
