.. _api-jobs:

========
Jobs API
========

The jobs API makes it easy to work with your spider's jobs.

jobs/schedule.json
------------------

Schedules a job for a given spider.

============ ==================================================================== ========
Parameter    Description                                                          Required
============ ==================================================================== ========
project      Project ID.                                                          Yes
job          Job ID.                                                              No
spider       Spider name.                                                         Yes
add_tag      Add specified tag to job                                             No
priority     Job priority. Support values: 0 (lowest) to 4 (highest). Default: 2. No
job_settings JSON array of job settings.                                          No
============ ==================================================================== ========

.. note:: Any other parameter will be treated as a spider argument.

====== ============================== =======================================
Method Description                    Supported parameters
====== ============================== =======================================
GET    Schedule the specified spider. project, job, spider, add_tag, priority
====== ============================== =======================================

Example::

	$ curl -u APIKEY: https://dash.scrapinghub.com/api/schedule.json -d project=123 -d spider=somespider -d add_tag=sometag -d spiderarg1=example -d job_settings='{ "setting1": "value1", "setting2": "value2" }'
	{"status": "ok", "jobid": "123/1/1"}


jobs/list.{json,jl}
--------------------

Retrieve job information for a given project, spider, or specific job.

========= ==================================== ========
Parameter Description                          Required
========= ==================================== ========
project   Project ID.                          Yes
job       Job ID.                              No
spider    Spider name.                         No
state     Return jobs with specified state     No
has_tag   Return jobs with specified tag.      No
lacks_tag Return jobs that lack specified tag. No
========= ==================================== ========

====== ========================= ===============================================
Method Description               Supported parameters
====== ========================= ===============================================
GET    Retrieve job information. project, job, spider, state, has_tag, lacks_tag
====== ========================= ===============================================

Examples::

  # Retrieve the latest 10 finished jobs
  $ curl -u APIKEY: "https://dash.scrapinghub.com/api/jobs/list.json?project=123&spider=somespider&state=finished&count=10"
  # Retrieve all running jobs
  $ curl -u APIKEY: "https://dash.scrapinghub.com/api/jobs/list.json?project=123&state=running" 
  # Retrieve all jobs with the tag ``consumed``
  $ curl -u APIKEY: "https://dash.scrapinghub.com/api/jobs/list.json?project=123&lacks_tag=consumed" 

jobs/update.json
----------------

Updates information about jobs.

========== ============================== ========
Parameter  Description                    Required
========== ============================== ========
project    Project ID.                    Yes
job        Job ID.                        Yes
add_tag    Add specified tag to job.      No
remove_tag Remove specified tag from job. No
========== ============================== ========

====== ======================= =================================
Method Description             Supported parameters
====== ======================= =================================
POST   Update job information. project, job, add_tag, remove_tag
====== ======================= =================================

Example::

  $ curl -u APIKEY: https://dash.scrapinghub.com/api/jobs/update.json -d project=123 -d job=123/1/2 -d add_tag=consumed

jobs/delete.json
----------------

Deletes one or more jobs.

=========  ============================== ========
Parameter  Description                    Required
=========  ============================== ========
project    Project ID.                    Yes
job        Job ID.                        Yes
=========  ============================== ========

====== ============== =================================
Method Description    Supported parameters
====== ============== =================================
POST   Delete job(s). project, job
====== ============== =================================

Example::

  $ curl -u APIKEY: https://dash.scrapinghub.com/api/jobs/delete.json -d project=123 -d job=123/1/2 -d job=123/1/3

jobs/stop.json
----------------

Stops one or more running jobs.

=========  ============================== ========
Parameter  Description                    Required
=========  ============================== ========
project    Project ID.                    Yes
job        Job ID.                        Yes
=========  ============================== ========

====== ============ =================================
Method Description  Supported parameters
====== ============ =================================
POST   Stop job(s). project, job
====== ============ =================================

Example::

  $ curl -u APIKEY: https://dash.scrapinghub.com/api/jobs/stop.json -d project=123 -d job=123/1/1 -d job=123/1/2

