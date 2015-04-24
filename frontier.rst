==============
Crawl Frontier
==============

The *Hub Crawl Frontier* (HCF) stores pages visited and outstanding requests to
make. It can be thought of as a persistent shared storage for a crawl scheduler.

Web pages are identified by a fingerprint. This can be the URL of the page, but
crawlers may use any other string (e.g. a hash of post parameters, if it
processes post requests), so there is no requirement for the fingerprint to be
a valid URL.

A project can have many frontiers and each frontier is broken down into slots.
A separate priority queue is maintained per slot. This means that requests
from each slot can be prioritized separately and crawled at different rates and
at different times.

Arbitrary data can be stored in both the crawl queue and with the set of
fingerprints.

A typical example would be to use the URL as a fingerprint and the hostname as
a slot. The crawler should ensure that each host is only crawled from one
process at any given time so that politeness can be maintained.

.. _frontier-api:

Frontier API
============

The following method enqueues a request if the fingerprint has not been seen
before and adds it to the set of fingerprints::

    $ curl -u <API_KEY>: -d '{"fp":"/some/path.html"}'  \
        https://storage.scrapinghub.com/hcf/78/test/s/example.com
    {"newcount":1}

The project ID is ``78``, the frontier is ``test`` and the slot is ``example.com``. Multiple
requests can be added at once by separating them by newlines (JSON Lines format). The
number of new requests that have been added to the frontier is returned in the
``newcount`` parameter.

Here are the fields that can be specified:

=====   ===========
field   description
=====   ===========
fp      Request fingerprint *(required)*
qdata   Data to be stored along with the fingerprint in the request queue
fdata   Data to be stored along with the fingerprint in the fingerprint set
p       Priority: lower priority numbers are returned first (default is ``0``)
=====   ===========

Here is a more complete example::

    $ curl -u <API_KEY>: -d $'{"fp":"/"}\n{"fp":"page1.html", "p": 1, "qdata": {"depth": 1}}' \
        https://storage.scrapinghub.com/hcf/78/test/s/example.com
    {"newcount":2}

By using the same priority as request depth, the website can be traversed in
breadth-first order from the starting URL.

Requests can be retrieved from the request queue::

    $ curl -u <API_KEY>: https://storage.scrapinghub.com/hcf/78/test/s/example.com/q
    {"id":"00013967d8af7b0001","requests":[["/",null]]}
    {"id":"01013967d8af7e0001","requests":[["page1.html",{"depth":1}]]}

This will retrieve batches of the next requests to be processed. Each line
represents a batch of requests and may contain up to 100 *[fingerprint, data]*
pairs. The requests with the lowest ``p`` parameter are first, with earlier
requests before later ones. If a ``mincount`` parameter is present, enough batches
will be retrieved to get at least this number of requests, therefore
``mincount`` *<= request count <=* ``mincount`` *+ 100*. If not present, all values are
returned.

Once a batch has been processed, clients should indicate that the batch is
completed so that it will be removed and no longer returned when new batches
are requested. This can be achieved by posting the IDs of the completed
batches::

    $ curl -u <API_KEY>: -d '"00013967d8af7b0001"' https://storage.scrapinghub.com/hcf/78/test/s/example.com/q/deleted

IDs can be specified as arrays, or as single values. As with the previous
examples, multiple lines of input is accepted.

This now leaves only a single batch remaining in the crawl queue::

    $ curl -u <API_KEY>: https://storage.scrapinghub.com/hcf/78/test/s/example.com/q
    {"id":"01013967d8af7e0001","requests":[["page1.html",{"depth":1}]]}

All fingerprints can be downloaded by requesting the fingerprint set::

    $ curl -u <API_KEY>: https://storage.scrapinghub.com/hcf/78/test/s/example.com/f
    {"fp":"/"}
    {"fp":"page1.html"}

They are ordered lexicographically by fingerprint value.


You can list the existing frontiers for a project::

    $ curl -u <API_KEY>: https://storage.scrapinghub.com/hcf/78/list
    ["test"]

And list the slots for a given frontier::

    $ curl -u $SHUB_APIKEY: https://storage.scrapinghub.com/hcf/78/test/list
    ["example.com"]

Slots can be deleted::

    $ curl -u <API_KEY>: -X DELETE https://storage.scrapinghub.com/hcf/78/test/s/example.com/

