.. _api-jobq:

========
JobQ API
========

It is often convenient to consume data from jobs once they finish running. The *JobQ API* can provide an ordered list of finished job keys, with the most recently finished first::

    $ curl -u APIKEY: https://storage.scrapinghub.com/jobq/53/list
    {"key":"53/7/81","ts":1397762393489}
    {"key":"53/7/80","ts":1395111612849}
    {"key":"53/7/78","ts":1393972804722}
    {"key":"53/7/77","ts":1393972734215}
    ...

A job key can be used with the items, logs or requests APIs to retrieve data, for example::

    $ curl -u APIKEY: https://storage.scrapinghub.com/items/53/7/81

This will get the items from the most recently finished job.

We recommend storing the key of the most recently finished job (``53/7/81`` in our example) along with the downloaded data. To update the dataset later, it is possible to list jobs, stopping at the previously downloaded job::

    $ curl -u APIKEY: "https://storage.scrapinghub.com/jobq/53/list?stop=53/7/81"
    {"key":"53/7/83","ts":1403610146780}
    {"key":"53/7/82","ts":1397827910849}

This retrieves all jobs that have finished since the specified job.

``ts`` is the timestamp at which the job was added to the finished queue. It is possible to return jobs finished between two timestamps::

    $ curl -u APIKEY: "https://storage.scrapinghub.com/jobq/53/list?startts=1359774955431&endts=1359774955440"
    {"key":"53/6/7","ts":1359774955439}
    {"key":"53/3/3","ts":1359774955437}
    {"key":"53/9/1","ts":1359774955431}

