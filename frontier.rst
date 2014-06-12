.. _frontier:

The Crawl Frontier
==================

The Hub Crawl Frontier (HCF) stores pages visited and outstanding requests to
make. It can be thought of as persistent shared storage for a crawl scheduler.

Web pages are identified by a fingerprint. This can be the URL of the page, but
crawlers may use something different (e.g. a hash of post parameters if it
processes post requests), so there is no requirement that this is a valid URL.

A project can have many frontiers and each frontier is broken down into slots.
A separate priority queue is maintained per slot. This means that requests
from each slot can be prioritized separately and crawled at different rates and
at different times.

Arbitrary data can be stored in both the crawl queue and with the set of
fingerprints.

A typical example would be to use the URL as a fingerprint and the hostname as
the slot. The crawler should ensure that each host is only crawled from one
process at any given time so that politeness can be maintained.

.. _frontier-api:

Frontier API
============

The following method enqueues a request if the fingerprint has not been seen
before and adds it to the set of fingerprints::

    $ curl -d '{"fp":"/some/path.html"}'  \
        http://storage.scrapinghub.com/hcf/78/test/s/example.com
    {"newcount":1}

The project id is 78, the frontier is 'test' and the slot is 'example.com'. Multiple
requests can be added at once by separating them by newlines (jsonlines format). The
number of new requests that have been added to the frontier is returned in the
newcount parameter.

Here are the fields that can be specified:

=====   ===========
field   description
=====   ===========
fp      Request fingerprint - required
qdata   data to be stored along with the fingerprint in the request queue
fdata   data to be stored along with the fingerprint in the fingerprint set
p       Priority - lower priority numbers are returned first. The default is 0.
=====   ===========

Here is a more complete example::

    $ curl -d $'{"fp":"/"}\n{"fp":"page1.html", "p": 1, "qdata": {"depth": 1}}' \
        http://storage.scrapinghub.com/hcf/78/test/s/example.com
    {"newcount":2}

by using the same priority as request depth, the website can be traversed in
breath first order from the starting URL.

Requests can be retrieved from the request queue::

    $ curl http://storage.scrapinghub.com/hcf/78/test/s/example.com/q
    {"id":"00013967d8af7b0001","requests":[["/",null]]}
    {"id":"01013967d8af7e0001","requests":[["page1.html",{"depth":1}]]}

This will retrieve batches of the next requests to be processed. Each line
represents a batch of requests and may contain up to 100 [fingerprint, data]
pairs. The requests with the lowest 'p' parameter are first, with earlier
requests before later ones. If a `mincount` parameter is present, enough batches
will be retrieved to get at least this number of requests, therefore
mincount <= request count <= mincount + 100. If not present, all values are
returned.

Once a batch has been processed, clients should indicate that the batch is
completed so that it will be removed and no longer returned when new batches
are requested.  This can be achieved by posting the ids of the completed
batches::

    $ curl -d '"00013967d8af7b0001"' http://storage.scrapinghub.com/hcf/78/test/s/example.com/q/deleted

Ids can be specified as arrays, or as single values. As with the previous
examples, multiple lines of input is accepted.

This now leaves only a single batch remaining in the crawl queue::

    $ curl http://storage.scrapinghub.com/hcf/78/test/s/example.com/q
    {"id":"01013967d8af7e0001","requests":[["page1.html",{"depth":1}]]}

All fingerprints can be downloaded by requesting the fingerprint set::

    $ curl http://storage.scrapinghub.com/hcf/78/test/s/example.com/f
    {"fp":"/"}
    {"fp":"page1.html"}

They are ordered lexographically by fingerprint value.

Slots can be deleted::

    $ curl -X DELETE http://storage.scrapinghub.com/hcf/78/test/s/example.com/

