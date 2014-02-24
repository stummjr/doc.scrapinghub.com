.. _api:

===
API
===

All API calls require authentication using either cookies (which you typically use from the browser) or `HTTP basic access authentication`_ (typically used from code). That means you can access the API from a browser (if you're already logged into Scrapinghub), but you'll need to use HTTP authentication if you're accessing it from command line or code.

To ensure reproducibility all examples use `curl`_, a widely available HTTP client.

We highly recommend installing the `JSONView extension`_ (available for Firefox and Chrome) for visualizing API responses.


Authentication
==============

There are several ways to authenticate the API calls. If you're using the browser to see the responses, then you'll be already authenticated using your web browser session. If you plan to access the API from another system (outside your browser), you can use two authentication mechanisms described below.

API Key with HTTP Auth
----------------------

You can use your API key instead of your credentials, and pass it using *HTTP Auth* with an empty password::

    curl https://dash.scrapinghub.com/api/jobs/list.json?project=123 -u e8f825a5fb634a8fa17f3ca54a5daa11:

You can get your API key from the Scrapinghub Dash by going to *Account -> API Keys*.

API Key as URL Argument
-----------------------

You can also pass your API key as a URL argument::

    curl https://dash.scrapinghub.com/api/jobs/list.json?project=123&apikey=e8f825a5fb634a8fa17f3ca54a5daa11


Spiders API
===========

spiders/list.json
-----------------

Retrieves information about spiders.

* Supported Request Methods: ``GET``

* Filtering parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``spider`` - a spider name

*Examples:*

To retrieve all spiders in project ``123``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/spiders/list.json?project=123"

To retrieve spiders ``aspider`` and ``bspider`` in project ``123``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/spiders/list.json?project=123&spider=aspider&spider=bspider"


Jobs API
========

.. _schedule-api:

schedule.json
-------------

Schedules a job.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``spider`` *(required)* - the spider name
  * ``add_tag`` - add tag to the job (can be given multiple times)
  * ``priority`` - set the job priority: possible values range from ``0`` (lowest priority) to ``4`` (highest priority), default is ``2``
  * any other parameter is passed as a spider argument

Example request::

    $ curl -u APIKEY: https://dash.scrapinghub.com/api/schedule.json -d project=123 -d spider=somespider -d add_tag=sometag

Example response::

    {"status": "ok", "jobid": "123/1/1"}

jobs/list.json
--------------

Retrieves information about jobs.

* Supported Request Methods: ``GET``

* Filtering parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``job`` - the ID of a specific job to retrieve
  * ``spider`` - a spider name (only jobs belonging to this spider will be returned)
  * ``state`` - return only jobs with this state, valid values: ``pending``, ``running``, ``finished``
  * ``has_tag`` - return only jobs containing the given tag (may be repeated)
  * ``lacks_tag`` - return only jobs not containing the given tag (may be repeated)

*Examples:*

To retrieve the latest 10 finished jobs in project ``123``, for spider ``somespider``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/jobs/list.json?project=123&spider=somespider&state=finished&count=10"

To retrieve all currently running jobs in project ``123``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/jobs/list.json?project=123&state=running"

To get all jobs not marked with tag ``consumed`` from project ``123``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/jobs/list.json?project=123&lacks_tag=consumed"

jobs/list.jl
------------

Similar to `jobs/list.json`_ but returns the jobs in `JSONLines format`_, which allows to retrieve all jobs without having to paginate the results.

The first line of the result is special and contains metadata.

See `jobs/list.json`_ for examples.

jobs/update.json
----------------

Updates information about jobs.

* Supported Request Methods: ``POST``

* Filtering parameters as for jobs/list.json

* Tagging parameters:

  * ``add_tag`` - adds the given tag to the projects selected by the filtering parameters
  * ``remove_tag`` - removes the given tag from the projects selected by the filter parameters

*Example:*

To mark job ``123/1/2`` of project ``123`` with the tag ``consumed``::

    curl -u APIKEY: https://dash.scrapinghub.com/api/jobs/update.json -d project=123 -d job=123/1/2 -d add_tag=consumed

jobs/delete.json
----------------

Deletes one or more jobs.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``job`` - the ID of a job to delete (can be repeated)

*Example:*

To delete jobs ``123/1/2`` and ``123/1/3`` of project ``123``::

    curl -u APIKEY: https://dash.scrapinghub.com/api/jobs/delete.json -d project=123 -d job=123/1/2 -d job=123/1/3

jobs/stop.json
----------------

Stops one or more running jobs.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``job`` - the ID of a job to stop (can be repeated)

*Example:*

To stop jobs ``123/1/1`` and ``123/1/2`` of project ``123``::

    curl -u APIKEY: https://dash.scrapinghub.com/api/jobs/stop.json -d project=123 -d job=123/1/1 -d job=123/1/2


Items API
=========

items.json
----------

Retrieves scraped items of a job.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``job`` or ``spider`` *(required)* - the job or spider to retrieve items from. If you specify a job, the items scraped on that job will be returned. If you specify a spider, the items scraped on the *last finished job* of that spider will be returned.
  * ``count`` and ``offset`` - see :ref:`pagination`

*Examples:*

To retrieve items scraped by job ``123/1/4``::

    curl -L -u APIKEY: "https://dash.scrapinghub.com/api/items.json?project=123&job=123/1/4"

.. warning:: This only returns the first 100 items. See :ref:`pagination`. If you want to return all items in one stream, you can use `items.jl`_.

To retrieve items scraped by the *last finished job* of the spider ``myspider``::

    curl -L -u APIKEY: "https://dash.scrapinghub.com/api/items.json?project=123&spider=myspider"

To retrieve the latest 20 items of job ``123/1/4`` *(this works even while the job is running)*::

    curl -L -u APIKEY: "https://dash.scrapinghub.com/api/items.json?project=123&job=123/1/4&count=-20"

items.jl
--------

Similar to `items.json`_ but returns the items in `JSONLines format`_, which allows to retrieve all items without having to paginate the results.

*Example:*

To retrieve all items scraped by job ``123/1/4``::

    curl -L -u APIKEY: "https://dash.scrapinghub.com/api/items.jl?project=123&job=123/1/4"

items.csv
---------

Similar in usage to `items.json`_ and `items.jl`_, but returns items in CSV format and requires two extra parameters: ``fields`` and ``include_headers``.

* Extra Parameters:

  * ``fields`` *(required)* - a comma separated list of item fields to include in the exported CSV file
  * ``include_headers`` *(required)* - ``1`` if the exported CSV file should contain fields' headers in the first row, ``0`` otherwise

*Examples:*

To retrieve all items scraped by job ``123/1/4``, this time in CSV format, and dump *name*, *url* and *price* fields, excluding headers::

    curl -L -u APIKEY: "https://dash.scrapinghub.com/api/items.csv?project=123&job=123/1/4&include_headers=0&fields=name,url,price"


Log API
=======

log.txt
-------

Retrieves the log of a job.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``job`` *(required)* - the job to retrieve items from
  * ``level`` - the minimum log level to return (if not given, returns all log levels)
  * ``count`` and ``offset`` - see :ref:`pagination`

*Example:*

To retrieve the log of job ``123/1/4`` in plain text format::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/log.txt?project=123&job=123/1/4"

log.json
--------

Similar to `log.txt` but returns the log entries as a list of JSON objects containing the properties ``logLevel``, ``message`` and ``time``.

log.jl
--------

Similar to `log.json` but returns the log entries in `JSONLines format`_.


.. _autoscraping-api:

Autoscraping API
================

as/project-slybot.zip
---------------------

Retrieves the project specifications in slybot format, zip compressed. By default includes the specification of all the spiders in the project.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``spider`` *(optional and multiple)* - if present, include only the specifications of given spiders

*Examples:*

To download the entire project with ID ``123`` (including all spiders)::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/as/project-slybot.zip?project=123"

To download only the spider with name ``myspider``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/as/project-slybot.zip?project=123&spider=myspider"

as/spider-properties.json
-------------------------

Retrieves or updates an autoscraping spider properties. If no update parameters are given, the call returns the current properties of the spider.

Retrieves an autoscraping spider properties.

* Supported Methods: ``GET``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``spider`` *(required)* - the spider name

Updates an autoscraping spider properties.

* Supported Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``spider`` *(required)* - the spider name
  * ``start_url`` *(optional and multiple)* - set the start URL and update ``start_urls`` property with the given values

*Examples:*

To get the properties of the spider ``myspider``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/as/spider-properties.json?project=123&spider=myspider"

To update the start URLs of a spider::

    curl -u APIKEY: -d project=123 -d spider=myspider \
            -d start_url=http://www.example.com/listA \
            -d start_url=http://www.example.com/listB \
            https://dash.scrapinghub.com/api/as/spider-properties.json


.. _eggs-api:

Eggs API
========

These API calls provide a means for uploading Python eggs (typically used for managing external dependencies) to a project.

eggs/add.json
-------------

Adds a Python egg to a project.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``name`` *(required)* - the egg name
  * ``version`` *(required)* - the egg version
  * ``egg`` *(required)* - the egg to add (a file upload)

*Example:*

To add ``somelib`` egg to project ``123``::

    curl -u APIKEY: https://dash.scrapinghub.com/api/eggs/add.json -F project=123 -F name=somelib -F version=1.0 -F egg=@somelib-1.0.py2.6.egg

eggs/delete.json
----------------

Deletes a Python egg from a project.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``name`` *(required)* - the egg name

*Example:*

To delete ``somelib`` egg from project ``123``::

    curl -u APIKEY: https://dash.scrapinghub.com/api/eggs/delete.json -d project=123 -d name=somelib

eggs/list.json
--------------

Lists the eggs contained in a project.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID

*Example:*

To list all eggs in project ``123``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/eggs/list.json?project=123"


.. _reports-api:

Reports API
===========

This API provides a means for uploading reports which are attached to a scraping job. Job reports can be accessed through the *Reports* tab on the job page.

Multiple reports can be attached to a single job. Each report is uniquely identified by a key (within a given job).

reports/add.json
----------------

Uploads a report and attaches it to a job. The supported formats are `reStructuredText`_ and plain text.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``job`` *(required)* - the ID of the job to which the report will be attached
  * ``key`` *(required)* - a key that uniquely identifies the report within the job
  * ``content`` *(required)* - the report content in the format specified by ``content_type`` parameter
  * ``content_type`` *(required)* - the format of the content: supported formats are ``text/x-rst`` for `reStructuredText`_ and ``text/plain`` for plain text

*Example:*

To upload a report contained in ``report.rst`` file (in `reStructuredText`_ format) to job ``123/1/4`` of project ``123``::

   curl -u APIKEY: https://dash.scrapinghub.com/api/reports/add.json -F project=123 -F job=123/1/4 -F key=qareport -F content_type=text/x-rst -F content=@report.rst


.. _pagination:

Paginating API Results
======================

All API calls that return multiple items in JSON format are limited to return 100 items per call, at most. These API calls support two parameters that can be used for paginating the results. Those are:

* ``count`` - limit the number of results to return: negative counts are supported as well making it possible to return the *latest* entries, instead of the first ones
* ``offset`` - a number of results to skip from the beginning


JSON Lines Format
=================

*JSON Lines* is a variation of JSON format, which is more convenient for streaming. It consists of one JSON object per line.

For example, this is JSON::

    [{"name": "hello", "price": "120"}, {"name": "world", "price": "540"}]

While this is the same data in JSON Lines format::

    {"name": "hello", "price": "120"}
    {"name": "world", "price": "540"}


To avoid memory problems, all API calls that return JSON data (for example, `items.json`_) are limited to a maximum of 100 results, and may need the client to paginate over them. However, this limitation doesn't apply to JSON Lines format (for example, `items.jl`).


Python Library
==============

There is a Python client library for Scrapinghub API available here:

    https://github.com/scrapinghub/python-scrapinghub


.. _curl: http://curl.haxx.se/
.. _HTTP Basic access authentication: http://en.wikipedia.org/wiki/Basic_access_authentication
.. _JSONView extension: http://benhollis.net/software/jsonview/
.. _reStructuredText: http://en.wikipedia.org/wiki/ReStructuredText
