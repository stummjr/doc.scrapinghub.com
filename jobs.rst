===========================
Running and monitoring jobs
===========================

There are two ways to run and monitor jobs in Scrapinghub: manually through
`dash` or programmatically via the Jobs API that is described below.

.. _jobs-api:

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

Similar to `jobs/list.json`_ but returns the jobs in jsonlines format, which allows to retrieve all jobs without having to paginate the results.

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


