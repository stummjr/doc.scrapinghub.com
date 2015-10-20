.. _api-items:

=========
Items API
=========

.. note:: Even though these APIs support writing, they are most often used for reading. The crawlers running on Scrapinghub cloud are the ones that write to these endpoints. However, both operations are documented here for completion.

Item object
-----------

=============== =======================================================================
Field           Description
=============== =======================================================================
_type           The item definition.
_template       The template matched against. Portia only.
_cached_page_id Cached page ID. Used to identify the scraped page in storage.
=============== =======================================================================

Scraped fields will be top level alongside the internal fields listed above.
 
items/:project_id[/:spider_id][/:job_id][/:item_no][/:field_name]
-----------------------------------------------------------------

Retrieve or insert items for a project, spider, or job. Where ``item_no`` is the index of the item.

========= ==================================================================== ========
Parameter Description                                                          Required
========= ==================================================================== ========
format    Results format. See :ref:`api-overview-resultformats`.               No
meta      Meta keys to show.                                                   No
nodata    If set, no data will be returned other than specified ``meta`` keys. No
========= ==================================================================== ========

============= ==========================================================
Header        Description
============= ==========================================================
Content-Range Can be used to specify a start index when inserting items.
============= ==========================================================

====== =================================================== ====================
Method Description                                         Supported parameters
====== =================================================== ====================
GET    Retrieve items for a given project, spider, or job. format, meta, nodata
POST   Insert items for a given job                        N/A
====== =================================================== ====================

GET examples::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7/fieldname                     # Retrieve value of a single field
    ...
    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7/0                             # Retrieve first item of a job
    ...
    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7                               # Retrieve all items for a job
    ...
    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34                                 # Retrieve all jobs for a spider
    ...
    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/                                   # Retrieve all jobs for a project
    ...
    $ curl -u APIKEY: "https://storage.scrapinghub.com/items/53/1/7?meta=_key&nodata=1"           # Use nodata parameter to show only specified meta key
    {"_key":"53/1/7/0"}
    {"_key":"53/1/7/1"}
    {"_key":"53/1/7/2"}
    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7 -H "Accept: application/json" # Retrieve items for a job in JSON format
    {"_key":"1111111/1/1/0"}

POST examples::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7 -X POST -T items.jl                                  # Add items to a job
    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7 -X POST -T items.jl -H "content-range: items 500-/*" # Use the Content-Range header to specify a start index

Where ``items.jl`` is a file containing the items in JSON lines format.

The API will only return ``200`` if the data was successfully stored. There's no limit on the amount of data you can send, but a ``HTTP 413`` response will be returned if any single item is over 1M.

items/:project_id/:spider_id/:job_id/stats
------------------------------------------

Retrieve the item stats for a given job.

=================== ==========================================
Field               Description
=================== ==========================================
counts[field]       The number of times the field was scraped.
totals.input_bytes  The total size of all items in bytes.
totals.input_values The total number of items.
=================== ==========================================

========= ================================= ========
Parameter Description                       Required
========= ================================= ========
all       Include hidden fields in results. No
========= ================================= ========

====== ====================
Method Supported parameters
====== ====================
GET    all
====== ====================

Example::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/34/7/stats
    {"counts":{"field1":9350,"field2":514},"totals":{"input_bytes":14390294,"input_values":10000}}
