.. _crawlera:

========
Crawlera
========

First Steps
===========

Testing Crawlera Credentials
----------------------------

.. code-block:: text

    curl -vx paygo.crawlera.com:8010 -U USER:PASS http://crawlera.com

Using Crawlera with Scrapy
--------------------------

Crawlera provides HTTP proxy interface allowing to employ Crawlera with any software supporting standard HTTP proxies. Scrapy supports HTTP proxies through the ``http_proxy`` environment variable, so it’s possible to use Crawlera this way::

    export http_proxy=http://USER:PASS@paygo.crawlera.com:8010

There is also the Crawlera middleware provided by the ``scrapylib`` library and enabled by adding the following lines to your Scrapy project settings::

    DOWNLOADER_MIDDLEWARES = {'scrapylib.crawlera.CrawleraMiddleware': 600}
    CRAWLERA_ENABLED = True
    CRAWLERA_USER = 'your_username'
    CRAWLERA_PASS = 'your_password'

When using Crawlera with Scrapy, it’s recommended to disable the :ref:`autothrottle-addon` extension and increase the maximum number of concurrent requests.

To enable Crawlera in `Dash <http://dash.scrapinghub.com/>`_, see :ref:`crawlera-scrapy-cloud` section.

Working with HTTPS
------------------

The caveat of Crawlera’s HTTP proxy interface is that it doesn’t support the *CONNECT* method, so HTTPS URLs won’t work with a standard proxy configuration. While some HTTP clients support HTTPS requests through standard HTTP proxies, resorting to the *fetch API* is suggested::

    curl -U USER:PASS http://paygo.crawlera.com/fetch?url=https://twitter.com

Note that it’s not recommended to use the *fetch API* with the Crawlera middleware. Another option is the :ref:`x-crawlera-use-https` header.


Errors
======

When an error occurs, Crawlera sends a response containing an :ref:`x-crawlera-error` header and an error message in the body.

.. note:: These errors are internal to Crawlera and are subject to change at any time, so should not be relied on and only used for debugging.

====================== =============  ======================
X-Crawlera-Error       Response Code  Error Message
====================== =============  ======================
bad_session_id         400            incorrect session id
user_session_limit     400            session limit exceeded
\                      407
\                      500            unexpected error
nxdomain               502            error looking up domain
econnrefused           502            connection refused
econnreset             502            connection reset
socket_closed_remotely 502            server closed socket connection
send_failed            502            send failed
noslaves               503            no available proxies
slavebanned            503            website crawl ban
serverbusy             503            server busy - too many outstanding requests
timeout                504            timeout from upstream server
msgtimeout             504            timeout processing http stream
\                      523            domain forbidden. please contact support@scrapinghub.com
\                      540            bad header value for *<some_header>*
====================== =============  ======================


Sessions and Request Limits
===========================

Sessions
--------

Crawlera supports sessions, allowing to use the same slave for every request. A session has a lifetime of one hour (counting from the time of the session's last use) and each Crawlera user is limited to 10 concurrent sessions.

Sessions are managed using the :ref:`x-crawlera-session` header. To create a new session send::

    X-Crawlera-Session: create

Crawlera will respond with the session ID in the same header::

    X-Crawlera-Session: <session ID>

From then onward, subsequent requests can be made through the same slave by sending the session ID in the request header::

    X-Crawlera-Session: <session ID>

If an incorrect session ID is sent, Crawlera responds with a ``bad_session_id`` error.

Request Limits
--------------

Crawlera’s default request limit is 5 requests per second (rps) for each website. There is a default delay of 200ms between each request and a default delay of 12s between requests through the same slave. These delays can differ for more popular domains. If the requests per second limit is exceeded, further requests will be delayed for up to 15 minutes. Each request made after exceeding the limit will increase the request delay. If the request delay reaches the soft limit (120 seconds), then each subsequent request will contain :ref:`x-crawlera-next-request-in` header with the calculated delay as the value.


Request Headers
===============

Crawlera supports a number of headers which can be used to control its behaviour.

X-Crawlera-NO-UA
-----------------
:sub:`Only available to Enterprise users.`

This header is deprecated, use :ref:`X-Crawlera-UA <x-crawlera-ua>` instead with the value ``pass``.

.. _x-crawlera-ua:

X-Crawlera-UA
--------------
:sub:`Only available to Enterprise users.`

This header controls Crawlera User-Agent behaviour. The supported values are:

* ``pass`` - pass the User-Agent as it comes on the client request
* ``crawlera`` - produce a User-Agent of type ``crawlera`` (e.g. ``Mozilla/5.0 (compatible; Crawlera/0.1; UID 10828)``)
* ``desktop`` - use a random desktop browser User-Agent
* ``mobile`` - use a random mobile browser User-Agent

If ``X-Crawlera-UA`` isn’t specified, it will default to ``crawlera``. If an unsupported value is passed in ``X-Crawlera-UA`` header, Crawlera replies with a ``540 Bad Header Value``.

More User-Agent types will be supported in the future (``chrome``, ``firefox``) and added to the list above.

X-Crawlera-No-Bancheck
-----------------------
:sub:`Only available to Enterprise users.`

This header instructs Crawlera not to check responses against its ban rules and pass any received response to the client. The presence of this header (with any value) is assumed to be a flag to disable ban checks.

*Example*::

    X-Crawlera-No-Bancheck: 1

X-Crawlera-Cookies
-------------------
:sub:`Only available to Enterprise users.`

This header allows to disable internal cookies tracking performed by Crawlera.

*Example*::

    X-Crawlera-Cookies: disable

.. _x-crawlera-session:

X-Crawlera-Session
-------------------
:sub:`Only available to Enterprise users.`

This header instructs Crawlera to use sessions which will tie requests to a particular slave until it gets banned.

*Example*::

    X-Crawlera-Session: create

When ``create`` value is passed, Crawlera creates a new session an ID of which will be returned in the response header with the same name. All subsequent requests should use that returned session ID to prevent random slave switching between requests. Crawlera sessions currently have maximum lifetime of 1 hour and each user is limited to a maximum of 10 concurrent sessions.

.. _x-crawlera-use-https:

X-Crawlera-Use-HTTPS
--------------------

This header forces Crawlera to retrieve the URL using HTTPS scheme instead of HTTP. For example, to fetch https://twitter.com::

    curl -x paygo.crawlera.com:8010 -U USER:PASS http://twitter.com -H x-crawlera-use-https:1

X-Crawlera-JobId
----------------

This header sets the job ID for the request (useful for tracking requests in the Crawlera logs).

*Example*::

    X-Crawlera-JobId: 999

X-Crawlera-Max-Retries
----------------------

This header limits the number of retries performed by Crawlera.

*Example*::

    X-Crawlera-Max-Retries: 1

Passing ``1`` in the header instructs Crawlera to do up to 1 retry. Default number of retries is 5 (which is also the allowed maximum value, the minimum being 0).

Response Headers
================

.. _x-crawlera-next-request-in:

X-Crawlera-Next-Request-In
--------------------------

This header is returned when response delay reaches the soft limit (120 seconds) and contains the calculated delay value. If the user ignores this header, the hard limit (1000 seconds) may be reached, after which Crawlera will return HTTP status code ``503`` with delay value in ``Retry-After`` header.

X-Crawlera-Debug
----------------

This header activates tracking of additional debug values which are returned in response headers. At the moment only ``request-time`` and ``ua`` values are supported, comma should be used as a separator. For example, to start tracking request time send::

    X-Crawlera-Debug: request-time

or, to track both request time and User-Agent send::

    X-Crawlera-Debug: request-time,ua

The ``request-time`` option forces Crawlera to output to the response header a request time (in seconds) of the last request retry (i.e. the time between Crawlera sending request to a slave and Crawlera receiving response headers from that slave)::

    X-Crawlera-Debug-Request-Time: 1.112218

The ``ua`` option allows to obtain information about the actual User-Agent which has been applied to the last request (useful for finding reasons behind redirects from a target website, for instance)::

    X-Crawlera-Debug-UA: Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN) AppleWebKit/533+ (KHTML, like Gecko)

.. _x-crawlera-error:

X-Crawlera-Error
----------------

This header is returned when an error condition is met, stating a particular Crawlera error behind HTTP status codes (4xx or 5xx). The error message is sent in the response body.

*Example*::

    X-Crawlera-Error: user_session_limit

.. note:: Returned errors are internal to Crawlera and are subject to change at any time, so should not be relied on.

.. _crawlera-scrapy-cloud:

Using Crawlera with Scrapy Cloud
================================

To employ Crawlera in Scrapy Cloud projects the *Crawlera* addon is used. Go to **Settings > Addons > Crawlera** to activate.

Settings
--------

========================= ===================================================
CRAWLERA_URL              proxy URL (default: ``http://paygo.crawlera.com:8010``)
CRAWLERA_ENABLED          tick the checkbox to enable Crawlera
CRAWLERA_USER             Crawlera username
CRAWLERA_PASS             Crawlera password
CRAWLERA_MAXBANS          number of bans to ignore before closing the spider (default: ``20``)
CRAWLERA_DOWNLOAD_TIMEOUT timeout for requests (default: ``1800``)
========================= ===================================================


Using Crawlera with Selenium and Polipo
=======================================

Since it's not so trivial to set up proxy authentication in Selenium, a popular option is to employ `Polipo <http://www.pps.univ-paris-diderot.fr/~jch/software/polipo/>`_ as a proxy. Update Polipo configuration file ``/etc/polipo/config`` to include Crawlera credentials (if the file is not present, copy and rename ``config.sample`` found in Polipo source folder)::

    parentProxy = "paygo.crawlera.com:8010"
    parentAuthCredentials = "USER:PASS"

For password safety reasons this content is displayed as ``(hidden)`` in the Polipo `web interface manager <http://www.pps.univ-paris-diderot.fr/~jch/software/polipo/polipo.html#Web-interface>`_. The next step is to specify Polipo proxy details in the Selenium automation script, e.g. for Python and Firefox:

.. literalinclude:: _static/crawlera-selenium.py
    :language: python


Basic Examples in Various Programming Languages
================================================

In the following examples we'll be making HTTPS requests to https://twitter.com through Crawlera. Note that HTTPS transfer is enabled by :ref:`x-crawlera-use-https` header. For this reason, indicating ``https://`` in URLs is not required.

Python
------

Making use of `Requests <http://docs.python-requests.org/en/latest/>`_ library:

.. literalinclude:: _static/crawlera-python-requests.py
    :language: python

PHP
---

Making use of `PHP binding <http://curl.haxx.se/libcurl/php/examples>`_ for *libcurl* library:

.. literalinclude:: _static/crawlera-php-binding.php
    :language: php

Ruby
----

Making use of `curb <https://github.com/taf2/curb>`_, a Ruby binding for *libcurl*:

.. literalinclude:: _static/crawlera-curb.rb
    :language: ruby

Making use of `typhoeus <https://github.com/typhoeus/typhoeus>`_, another Ruby binding for *libcurl*:

.. literalinclude:: _static/crawlera-typhoeus.rb
    :language: ruby

Node.js
-------

Making use of `request <https://github.com/request/request>`_, an HTTP client:

.. literalinclude:: _static/crawlera-request.js
    :language: javascript

Java
----

Quoting from an example published at `The Apache HttpComponents™ <http://hc.apache.org/httpcomponents-client-ga/examples.html>`_ project website and inserting Crawlera details:

.. literalinclude:: _static/crawlera-httpc.java
    :language: java

C#
--

.. literalinclude:: _static/crawlera-proxy.cs
    :language: csharp


FAQ
===

How Do I Add My Own Proxies to Crawlera?
----------------------------------------

You can send a list of proxies (hosts/IPs) along with your Crawlera username to crawlera@scrapinghub.com, or you can create a private ticket in `UserEcho <http://support.scrapinghub.com/>`_.

How Do I Change My User-Agent?
------------------------------

To change your User-Agent you will need to use the :ref:`x-crawlera-ua` header with value ``pass``. This will instruct Crawlera to use the User-Agent header you send in the request.

How Do I Find the Slave Used in a Request?
------------------------------------------

Crawlera sends ``X-Crawlera-Slave`` response header containing the IP address and port of the slave used to make the request.

How Do I Measure Crawlera's Speed for a Particular Domain?
----------------------------------------------------------

You can use `crawlera-bench tool <https://github.com/scrapinghub/crawlera-tools>`_.

Where Can I Monitor My Crawlera Usage?
--------------------------------------

On your main page in Dash (i.e. ``https://dash.scrapinghub.com/USERNAME/``), under **Crawlera** you should see Crawlera users linked to your Dash account. If you click on a user, you will be able review the number of requests per day/month for that user.
