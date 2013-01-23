.. _api:

===
API
===

All API calls require authentication using either cookies (which you typically
use from the browser) or `HTTP Basic access authentication`_ (typically used
from code). That means you can access the API from a browser (if you're already
logged into Scrapinghub but you'll need to use HTTP authentication if you're
accessing it from command line or code.

All examples use `curl`_, a widely available HTTP client, to illustrate with a
reproducible example.

We highly recommend installing the `JSONView extension`_ (available for Firefox
and Chrome) for visualizing API responses.

Authentication
==============

There are several ways to authenticate the API calls. First, if you're using
the browser to see the responses, then you'll be authenticated already using
your web browser session.

If you plan to access the API from another system (outside your browser) you
can use 3 authentication mechanisms:

Credentials with HTTP Auth
--------------------------

You can pass your Scrapinghub credentials using HTTP auth. For example, with
curl::

    curl http://panel.scrapinghub.com/api/jobs.json?project=123 -u john:secret

API key with HTTP Auth
----------------------

You can use your API key instead of your credentials, and pass it using HTTP
auth with an empty password::

    curl http://panel.scrapinghub.com/api/jobs.json?project=123 -u e8f825a5fb634a8fa17f3ca54a5daa11:

You can get your API key from the Panel by going to Account -> API Keys.

API key as URL argument
-----------------------

You can also pass your API key as a URL argument::

    curl http://panel.scrapinghub.com/api/jobs.json?project=123&apikey=e8f825a5fb634a8fa17f3ca54a5daa11

Spiders API
===========

spiders/list.json
-----------------

Retrieve information about spiders.

* Supported Request Methods: ``GET``

* Filtering parameters:

  * ``project`` (required) - the project numeric id

  * ``spider`` - a spider name

Examples:

To retrieve the all spiders in project ``123``::

    curl -u USER:PASS "http://panel.scrapinghub.com/api/spiders/list.json?project=123"

To retrieve spiders ``aspider`` and ``bspider`` for project ``123``::

    curl -u USER:PASS "http://panel.scrapinghub.com/api/spiders/list.json?project=123&spider=aspider&spider=bspider"


Jobs API
========

schedule.json
-------------

Schedules a job.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` (required) - the project numeric id
  * ``spider`` (required) - the spider name
  * ``add_tag`` (optional) - add tag to the job (can be given multiple times)
  * any other parameter is passed as spider argument

Example request::

    $ curl http://panel.scrapinghub.com/api/schedule.json -d project=123 -d spider=somespider -d add_tag=sometag

Example response::

    {"status": "ok", "jobid": "4c59c056bda4a41f9d000002"}

jobs/list.json
--------------

Retrieve information about jobs.

* Supported Request Methods: ``GET``

* Filtering parameters:

  * ``project`` (required) - the project numeric id

  * ``job`` - the id of a specific job to retrieve

  * ``after`` - a job id. Only jobs whose id is greater than this will be
    returned. The job ids are generated using timestamps, so a job scheduled
    after another one will have a greater job id.

  * ``spider`` - a spider name. On jobs belonging to this spider will be
    returned
  
  * ``state`` - return only jobs with this state. Valid values: ``pending``,
    ``running``, ``finished``

  * ``has_tag`` - return only jobs containing the given tag. May be repeated.

  * ``lacks_tag`` - return only jobs not containing the given tag. May be repeated.

* Pagination parameters:

  * ``count`` and ``offset`` - see :ref:`pagination`

* Tagging parameters (``POST`` requests only):

  * ``add_tag`` - adds the given tag to the projects selected by the filtering
    parameters

  * ``remove_tag`` - removes the given tag to the projects selected by the
    filter parameters

Examples:

To retrieve the latest 10 finished jobs in project ``123``, for spider ``somespider``::

    curl -u USER:PASS "http://panel.scrapinghub.com/api/jobs/list.json?project=123&spider=somespider&state=finished&count=-10"

To retrieve all currently running jobs for project ``123``::

    curl -u USER:PASS "http://panel.scrapinghub.com/api/jobs/list.json?project=123&state=running"

To retrieve all jobs from project ``123``, finished after job ``4ca37770a1a3a24c45000005``::

    curl -u USER:PASS "http://panel.scrapinghub.com/api/jobs/list.json?project=123&state=finished&after=4ca37770a1a3a24c45000005"

To get all jobs not marked with tag ``consumed`` from project ``123``::

    curl -u USER:PASS "http://panel.scrapinghub.com/api/jobs/list.json?project=123&lacks_tag=consumed"

jobs/list.jl
------------

Similar to `jobs/list.json`_ but returns the jobs in `JSONLines format`_, which
allows to retrieve all jobs without having to paginate the results.

The first line of the result is special and contains metadata (like total
number of jobs).

See `jobs/list.json` for examples.

jobs/update.json
----------------

Updates information about jobs.

* Supported Request Methods: ``POST``

* Filtering parameters as for jobs/list.json

* Tagging parameters:

  * ``add_tag`` - adds the given tag to the projects selected by the filtering
    parameters

  * ``remove_tag`` - removes the given tag to the projects selected by the
    filter parameters

Example:

To mark job ``4ca37770a1a3a24c45000005`` of project ``123`` with the tag ``consumed``::

    curl -u USER:PASS http://panel.scrapinghub.com/api/jobs/update.json -d project=123 -d job=4ca37770a1a3a24c45000005 -d add_tag=consumed"

jobs/delete.json
----------------

Delete one or more jobs.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` (required) - the project numeric id

  * ``job`` - the id of a job to delete. Can be repeated.

Example:

To delete jobs ``4ca37770a1a3a24c45000005`` and ``4ca33330a1a3a24c45000005`` of
project ``123``::

    curl -u USER:PASS http://panel.scrapinghub.com/api/jobs/delete.json -d project=123 -d job=4ca37770a1a3a24c45000005 -d job=4ca33330a1a3a24c45000005"


jobs/stop.json
----------------

Stop one or more running jobs.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` (required) - the project numeric id

  * ``job`` - the id of a job to stop. Can be repeated.

Example:

To stop jobs ``4ca37770a1a3a24c45000005`` and ``4ca33330a1a3a24c45000005`` of
project ``123``::

    curl -u USER:PASS http://panel.scrapinghub.com/api/jobs/stop.json -d project=123 -d job=4ca37770a1a3a24c45000005 -d job=4ca33330a1a3a24c45000005"

Items API
=========

items.json
----------

Retrieve the scraped items of a job.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` (required) - the project numeric id

  * ``job`` or ``spider`` (required) - the job or spider to retrieve items
    from. If you specify a job, the items scraped on that job will be returned.
    If you specify a spider, the items scraped on the *last finished job* of
    that spider will be returned.

  * ``count`` and ``offset`` - see :ref:`pagination`

Examples:

To retrieve the items scraped by job ``4ca37770a1a3a24c45000005``::

    curl -L -u USER:PASS "http://panel.scrapinghub.com/api/items.json?project=123&job=4ca37770a1a3a24c45000005"

.. warning:: This only returns the first 100 items. See :ref:`pagination`. If
   you want to return all items in one stream, you can use `items.jl`_.

To retrieve the items scraped by the last job of the spider ``myspider``::

    curl -L -u USER:PASS "http://panel.scrapinghub.com/api/items.json?project=123&spider=myspider"

To retrieve the latest 20 items of job ``4ca37770a1a3a24c45000005`` (*this
works even if the job is running*)::

    curl -L -u USER:PASS "http://panel.scrapinghub.com/api/items.json?project=123&job=4ca37770a1a3a24c45000005&count=-20"

items.jl
--------

Similar to `items.json`_ but returns the items in `JSONLines format`_, which
allows to retrieve all items without having to paginate the results.

Examples:

To retrieve all items scraped by job ``4ca37770a1a3a24c45000005``::

    curl -L -u USER:PASS "http://panel.scrapinghub.com/api/items.jl?project=123&job=4ca37770a1a3a24c45000005"

items.csv
---------

Similar in usage to `items.json`_ and `items.jl`_, but returns items in CSV format and requires two extra parameters *fields*
and *include_headers*.

* Extra Parameters:

    * ``fields`` (required) - a comma separated list of item fields to include in the exported csv file.

    * ``include_headers`` (required) - Either ``0`` or ``1``. If ``1``, inserts a first row with fields headers in CSV.

Examples:

To retrieve all items scraped by job ``4ca37770a1a3a24c45000005``, this time in CSV format, no header, and dump name, url and price
fields::

    curl -L -u USER:PASS "http://panel.scrapinghub.com/api/items.csv?project=123&job=4ca37770a1a3a24c45000005&include_headers=0&fields=name,url,price"

Log API
=======

log.txt
-------

Retrieve the log of a job.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` (required) - the project numeric id

  * ``job`` (required) - the job to retrieve items from

  * ``level`` - the minimum log level to return. If not given, returns all log levels.

  * ``count`` and ``offset`` - see :ref:`pagination`

Examples:

To retrieve the log of job ``4ca37770a1a3a24c45000005`` in plain text format::

    curl -u USER:PASS "http://panel.scrapinghub.com/api/log.txt?project=123&job=4ca37770a1a3a24c45000005"

log.json
--------

Similar to `log.txt` but returns the log entries as a list of JSON objects
containing the properties: ``logLevel``, ``message`` and ``time``.

log.jl
--------

Similar to `log.json` but returns the log entries in `JSONLines format`_.

.. _autoscraping-api:

Autoscraping API
================

as/project-slybot.zip
---------------------

Retrieves the project specifications in slybot format, zip compressed. By default includes the specification of all the spiders in
the project.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` (required) - the project numeric id

  * ``spiders`` (optional) - a comma separated list of spiders. If present, include only the specifications of given spiders.


.. _eggs-api:

Eggs API
========

This API calls are used for uploading Python eggs related to a project,
typically used for managing external dependencies.

eggs/add.json
-------------

Add a Python egg to the project.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` (required) - the project numeric id

  * ``name`` (required) - the egg name

  * ``version`` (required) - the egg version

  * ``egg`` (required) - the egg to add (a file upload)

Examples:

To add an egg to a project::

    curl -u USER:PASS http://panel.scrapinghub.com/api/eggs/add.json -F project=123 -F name=somelib -F version=1.0 -F egg=@somelib-1.0.py2.6.egg

eggs/delete.json
----------------

Delete a Python egg from the project.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` (required) - the project numeric id

  * ``name`` (required) - the egg name


Examples:

To add an egg from a project::

    curl -u USER:PASS http://panel.scrapinghub.com/api/eggs/delete.json -d project=123 -d name=somelib

eggs/list.json
--------------

List eggs contained in a project.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` (required) - the project numeric id

Examples:

To add an egg from a project::

    curl -u USER:PASS "http://panel.scrapinghub.com/api/eggs/list.json?project=123"

.. _reports-api:

Reports API
===========

This API allows you to upload reports which are attached to scraping job. Job
reports can be accessed through the "Reports" tab in the job page.

Multiple reports can be attached to a single job. Each report is uniquely
identified by a key (within a given job).

reports/add.json
----------------

Upload a report and attach it to a job. The supported formats are
`reStructuredText`_ plain text.

* Supported Request Methods: ``POST``
* Parameters:
   * ``project`` (required) - the project numeric id
   * ``job`` (required) - the job id to which the report will be attached
   * ``key`` (required) - a key that uniquely identifies the report within the job
   * ``content`` (required) - the report content in the format specified by
     ``content_type`` parameter
   * ``content_type`` (required) - the format of the content. Supported formats
     are ``text/x-rst`` for `reStructuredText`_ and ``text/plain`` for plain
     text.

Example to upload a report assuming you have the report content (in
`reStructuredText`_ format) in a ``report.rst`` file::

   curl -u USER:PASS http://panel.scrapinghub.com/api/reports/add.json -F project=123 -F job=4fb0e9e5bbddbd7b460005f2 -F key=qareport -F content_type=text/x-rst -F @report.rst

.. _pagination:

Paginating API results
======================

All API calls that return multiple items in JSON format are limited to return
100 items per call, at most. These API calls support two parameters that can be
used for paginating the results. Those are:

* ``count`` - limits the number of results to return. Negative counts are
  supported and means returning the *latest* entries, instead of the first
  ones.

* ``offset`` - a number of results to skip from the beginning.


JSONLines format
================

JSON lines format is a variation of the JSON format, which is more friendly for
streaming. It consists of one JSON object per line.

For example, this is JSON::

    [{"name": "hello", "price": "120"}, {"name": "world", "price": "540"}]

While this is the same data in jsonlines format::

    {"name": "hello", "price": "120"}
    {"name": "world", "price": "540"}


To avoid memory problems, all API calls that return JSON data (for example,
`items.json`_) are limited to a maximum of 100 results, and may need the client
to paginate over them. However, this limitation doesn't apply to jsonlines
format (for example, `items.jl`).


Python library
==============

There is a Python client library for Scrapinghub API available here:

    https://github.com/scrapinghub/python-scrapinghub


.. _curl: http://curl.haxx.se/
.. _HTTP Basic access authentication: http://en.wikipedia.org/wiki/Basic_access_authentication
.. _JSONView extension: http://benhollis.net/software/jsonview/
.. _reStructuredText: http://en.wikipedia.org/wiki/ReStructuredText
