.. _api-eggs:

========
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


