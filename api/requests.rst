.. _api-requests:

============
Requests API
============

The requests API allows you to work with request and response data from your crawls.

Request object
--------------

======== ==================================== ========
Field    Description                          Required
======== ==================================== ========
parent   The index of the parent request.     No
duration Request duration in milliseconds.    Yes
status   HTTP response code.                  Yes
method   HTTP method. Default: GET            No
rs       Response size in bytes.              Yes
url      Request URL.                         Yes
fp       Request fingerprint.                 No
======== ==================================== ========

.. note:: Seed requests from start URLs will have no parent field.

requests/:project_id/:spider_id/:job_id
---------------------------------------

Retrieve or insert request data.

Here is an example of reading data::

    $ curl -u APIKEY: https://storage.scrapinghub.com/requests/53/34/7
    {"parent":0,"duration":12,"status":200,"method":"GET","rs":1024,"url":"http://scrapy.org/","time":1351521736957}

.. note:: Pagination and meta parameters are supported, see :ref:`api-overview-pagination` and :ref:`api-overview-metapar`.

POST example::

    $ curl -u APIKEY: https://storage.scrapinghub.com/requests/53/34/7 -X POST -T requests.jl

requests/:project_id/:spider_id/:job_id/stats
---------------------------------------------

Retrieve request stats for a given job.

=================== ========================================
Field               Description
=================== ========================================
counts[field]       The number of times the field occurs.
totals.input_bytes  The total size of all requests in bytes.
totals.input_values The total number of requests.
=================== ========================================

GET example::

    $ curl -u APIKEY: https://storage.scrapinghub.com/requests/53/34/7/stats
    {"counts":{"url":21,"parent":19,"status":21,"method":21,"rs":21,"duration":21,"fp":21},"totals":{"input_bytes":2397,"input_values":21}}

