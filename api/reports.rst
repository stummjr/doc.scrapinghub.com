.. _api-reports:

===========
Reports API
===========

.. note:: Job reports are deprecated.

You can use the reports API to upload and publish custom reports attached to jobs. Job reports can be accessed through the **Reports** tab of each job page in Dash.

Multiple reports can be attached to a job. Each report is uniquely identified by a key (within a given job).

reports/add.json
----------------

Upload a report and attach it to a job. The supported formats are `reStructuredText`_ and plain text.

============ ====================================================== ========
Parameter    Description                                            Required
============ ====================================================== ========
project      Project ID.                                            Yes
job          Job ID.                                                Yes
key          Report key.                                            Yes
content      Report content.                                        Yes
content_type Report content type. Supported: text/x-rst, text/plain Yes
============ ====================================================== ========

====== ========================================
Method Supported Parameters
====== ========================================                 
POST   project, job, key, content, content_type
====== ========================================

POST example::

   $ curl -u APIKEY: https://dash.scrapinghub.com/api/reports/add.json -F project=123 -F job=123/1/4 -F key=qareport -F content_type=text/x-rst -F content=@report.rst

.. _reStructuredText: http://en.wikipedia.org/wiki/ReStructuredText
