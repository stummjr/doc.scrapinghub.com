.. _api-collections:

===========
Collections
===========

Scrapinghub's *Collections* provide a way to store an arbitrary number of records indexed by
a key. They're often used by Scrapinghub projects as a single place to write
information from multiple scraping jobs.

The :ref:`collections-api` is described below.

.. _collections-api:

Collections API
---------------

The *Collections API* allows storing arbitrary objects in named sets. For example::

    $ curl -X POST -d '{"_key": "foo", "value": "bar"}' \
        https://storage.scrapinghub.com/collections/78/s/my_collection

Posts an object to the ``my_collection`` collection. A string ``_key`` field must be specified and
multiple objects may be posted when separated by newlines.

In the above example, the ``/s/`` in the path represents the collection type. The following
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

    $ curl https://storage.scrapinghub.com/collections/78/s/my_collection/foo
    {"value":"bar"}

Or they can be retrieved using a key parameter, which can be present multiple times::

    $ curl https://storage.scrapinghub.com/collections/78/s/my_collection?key=foo1&key=foo2
    {"value":"bar1"}
    {"value":"bar2"}

And the value of an item field can also be retrieved (text & HTML MIME types supported)::

    $ curl https://storage.scrapinghub.com/collections/78/s/my_collection/foo/value
    bar

Pagination and meta parameters are supported, see :ref:`pagination` and
:ref:`metapar`.

However, there is an additional filter that allows efficient filtering on key
prefixes::

    $ curl https://storage.scrapinghub.com/collections/78/s/my_collection?prefix=f
    {"value":"bar"}

Prefix filters should be used where possible as they use indexes, unlike other filters. Prefixes may be repeated and a ``prefixcount`` parameter may be used to specify the maximum number of values to return for each prefix.

You can also filter by records updated since a given timestamp::

    $ curl https://storage.scrapinghub.com/collections/78/s/my_collection?startts=1402699941000
    {"value":"bar"}

A common pattern is to download all changes between two timestamps using ``startts`` and ``endts`` parameters and the current timestamp can first be retrieved if necessary::

    $ curl https://storage.scrapinghub.com/system/ts
    1403039369570
    $ curl 'https://storage.scrapinghub.com/collections/78/s/my_collection?startts=1402699941000&endts=1403039369570'
    {"value":"bar"}

Timestamp filters are best used when fetching a large number of records and may have poor performance when selecting a very small number of records from a large collection.

