===========
Job Reports
===========

.. note:: Job reports are deprecated.

Using the :ref:`reports-api` you can upload and publish custom reports attached to jobs.

.. _reports-api:

Reports API
===========

This API provides means for uploading reports attached to a single scraping job:

 * Job reports can be accessed through the **Reports** tab of each job page in Dash.

Multiple reports can be attached to a job. Each report is uniquely identified by a key (within a given job).

reports/add.json
----------------

Uploads a report and attaches it to a job. The supported formats are `reStructuredText`_ and plain text.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``job`` *(required)* - the ID of the job to which the report will be attached
  * ``key`` *(required)* - a key that uniquely identifies the report within a job
  * ``content`` *(required)* - the report content in the format specified by ``content_type`` parameter
  * ``content_type`` *(required)* - the format of the content, supported formats are ``text/x-rst`` for reStructuredText and ``text/plain`` for plain text

*Example:*

To upload a job report contained in ``report.rst`` (a file in reStructuredText format) to job ``123/1/4`` of project ``123``::

   curl -u APIKEY: https://dash.scrapinghub.com/api/reports/add.json -F project=123 -F job=123/1/4 -F key=qareport -F content_type=text/x-rst -F content=@report.rst

.. _reStructuredText: http://en.wikipedia.org/wiki/ReStructuredText
