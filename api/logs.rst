.. _api-logs:

Logs API
========

Example log record::

    {"message": "Spider opened", "level": 20, "time": 1338987938007}

All log data is returned in plain text format (one row per log) unless another
format is specified in the *Accept* header. The following formats are
supported at present::

- application/x-jsonlines
- application/json
- application/xml
- text/plain
- text/csv

For example, to get logs in JSON Lines format using *curl*::

    $ curl -u APIKEY: https://storage.scrapinghub.com/logs/1111111/1/1/ -X GET -H "Accept: application/x-jsonlines"

As is the case with job data, the *Accept* header can be substituted with the
``format`` parameter::

    $ curl -u APIKEY: "https://storage.scrapinghub.com/logs/1111111/1/1?format=jl" -X GET

CSV output accepts the same options as with items (``fields`` and
``include_headers`` parameters) with the exception that ``fields`` is now optional and
defaults to ``time,level,message`` (all headers).

Like items, logs are also added by POSTing data to a particular job, for example::

    $ curl -u APIKEY: https://storage.scrapinghub.com/logs/53/34/7 -X POST -T log.jl

With the restriction that the records in the *log.jl* file must contain the
following fields:

* time *(number)* - the UNIX timestamp of the log message in *milliseconds* (must
  be integer)

* level *(number)* - the numeric value of the log level as defined in the Python
  logging library

* message *(string)* - the log message

Pagination and meta parameters are supported, see :ref:`pagination` and
:ref:`metapar`.

