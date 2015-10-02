.. _api-comments:

============
Data Reviews
============

Scrapy Cloud's *Data Reviews* allow you to reduce the time it takes to QA and
review data, by commenting directly on the scraped data itself.
 
.. image:: _static/data-reviews.png

With *Data Reviews* you can:

* Insert or extract comments using the Scrapinghub API.
* Comment on whole items or just on fields, to restrict or expand the scope of your scrapes.
* Archive comments that are no longer relevant.
* See where comments have been added right from the **Jobs** screen.

.. _comments-api:

Comments API
============

Comments are dicts like this one::

    {
        "id": int
        "created": datetime,
        "archived": datetime or null,
        "author": string,
        "avatar": string (user gravatar url),
        "text": string,
        "editable": bool,
    }


comments/<comment_id>
---------------------

Edits or archives a comment.

* Supported Request Methods: ``PUT``, ``DELETE``

* Parameters:

  * ``comment_id`` *(required)* - the comment's numeric ID

*Examples:*

To update the text of the comment ``12``::

    curl -X PUT -u APIKEY: --data 'text=my+new+text' "https://dash.scrapinghub.com/api/comments/12"

To archive the comment ``12``::

    curl -X DELETE -u APIKEY: "https://dash.scrapinghub.com/api/comments/12"


comments/<project_id>/<spider_id>/<job_id>
------------------------------------------

Retrieves all comments for a job indexed by item or item/field.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project_id`` *(required)* - the project's numeric ID
  * ``spider_id`` *(required)* - the spider's numeric ID
  * ``job_id`` *(required)* - the job's numeric ID

*Examples:*

To retrieve comments for the job ``12`` of the spider ``13`` of the project ``14``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/comments/14/13/12"

This will return a JSON like::

    {
        "0": [comment, comment, etc...],
        "0/title": [comment, comment, etc...],
        "12/url": [comment, comment, etc...],
    }

Where comment is a comment dict as defined above.


comments/<project_id>/stats
---------------------------

Retrieves the number of items with unarchived comments for each job of the project.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project_id`` *(required)* - the project's numeric ID

*Examples:*

To retrieve the number of items with unarchived comments for each job of the project ``51``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/comments/51/stats"

This will return a JSON like::

    {
        "51/422/2": 1,
        "51/414/2": 1,
        "51/421/2": 1,
        "51/423/2": 4,
        "51/413/3": 3,
        "51/418/2": 1
    }

The keys are *<project_id>/<spider_id>/<job_id>*.


comments/<project_id>/<spider_id>/<job_id>/<item_id>[/<field>]
--------------------------------------------------------------

Retrieves, updates or archives comments.

* Supported Request Methods: ``GET``, ``POST``, ``DELETE``

* Parameters:

  * ``project_id`` *(required)* - the project's numeric ID
  * ``spider_id`` *(required)* - the spider's numeric ID
  * ``job_id`` *(required)* - the job's numeric ID
  * ``item_id`` *(required)* - the item's numeric ID
  * ``field`` *(optional)* - the field's name

*Examples:*

To retrieve comments for the item ``11`` of the job ``12`` of the spider ``13`` of the project ``14``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/comments/14/13/12/11"

To retrieve comments for the field ``logo`` of the item ``11`` of the job ``12`` of the spider ``13`` of the project ``14``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/comments/14/13/12/11/logo"

To archive all comments for the item ``11`` of the job ``12`` of the spider ``13`` of the project ``14``::

    curl -X DELETE -u APIKEY: "https://dash.scrapinghub.com/api/comments/14/13/12/11"

To archive all comments for the field ``logo`` of the item ``11`` of the job ``12`` of the spider ``13`` of the project ``14``::

    curl -X DELETE -u APIKEY: "https://dash.scrapinghub.com/api/comments/14/13/12/11/logo"

To add a comment for the item ``11`` of the job ``12`` of the spider ``13`` of the project ``14``::

    curl -X POST --data 'text=some+text' -u APIKEY: "https://dash.scrapinghub.com/api/comments/14/13/12/11"

To add a comment for the field ``logo`` of the item ``11`` of the job ``12`` of the spider ``13`` of the project ``14``::

    curl -X POST --data 'text=some+text' -u APIKEY: "https://dash.scrapinghub.com/api/comments/14/13/12/11/logo"

