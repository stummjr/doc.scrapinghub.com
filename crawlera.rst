.. _crawlera:

========
Crawlera
========

Crawlera is a HTTP/HTTPS downloader that routes your requests through a pool of IP addresses, introducing delays and discarding IPs where necessary to evade anti-crawling measures. Crawlera is useful if you want to save yourself the hassle of tinkering with download delays and concurrent requests, dealing with cookies, user-agents, referrers to avoid getting banned.

First Steps
===========

Creating a Crawlera User
------------------------

You can create a new Crawlera user from the homepage by clicking the 'Add a crawlera user' button. A modal will pop up. Select the organization you wish to assign the user to and click 'Create'. Your Crawlera user will take the name of the organization you selected. You can only create one Crawlera user per organization, if you wish to create more than one please contact support.

The API key for your Crawlera user can be found on the user's 'Details' page. When authenticating use your API key as the username, leaving the password blank. You can also generate a new API key if you wish.

Testing Credentials
-------------------

You can test your credentials using ``curl``, like so:

.. code-block:: text

    curl -vx proxy.crawlera.com:8010 -U <API key>: http://www.food.com/


Using Crawlera with Scrapy
--------------------------

You can use Crawlera with Scrapy with the Crawlera middleware provided by the ``scrapy-crawlera`` library::

    pip install scrapy-crawlera

You can enable the middleware by adding the following lines to your Scrapy project settings::

    DOWNLOADER_MIDDLEWARES = {'scrapy_crawlera.CrawleraMiddleware': 600}
    CRAWLERA_ENABLED = True
    CRAWLERA_USER = '<API key>'
    CRAWLERA_PASS = ''

To achieve higher crawl rates when using Crawlera with Scrapy, it’s recommended to disable the :ref:`autothrottle-addon` extension and increase the maximum number of concurrent requests. You may also want to increase the download timeout. Here's an example::

    CONCURRENT_REQUESTS = 32
    CONCURRENT_REQUESTS_PER_DOMAIN = 32
    AUTOTHROTTLE_ENABLED = False
    DOWNLOAD_TIMEOUT = 600

To enable Crawlera via `Scrapinghub dashboard <http://dash.scrapinghub.com/>`_, see :ref:`crawlera-scrapy-cloud` section.

.. _working-with-https:

Working with HTTPS
------------------

Crawlera provides four ways for working with HTTPS:

#. CONNECT method (standard mechanism used by browsers)
#. the :ref:`x-crawlera-use-https` header
#. the :ref:`fetch-api`
#. HTTPs request over HTTP proxy

To use CONNECT method you need to download and install the certificate file for Crawlera Certificate Authority or disable
SSL certificate verification in your HTTP client.

The Crawlera Certificate authority can be downloaded here: :download:`crawlera-ca.crt`

.. _working-with-cookies:

Working with Cookies
--------------------

Crawlera manages cookies for you by default and retains them for up to 15 minutes since the last request. Crawlera keeps separate groups of cookies per outgoing node, and as a result consecutive requests will almost always have different cookies, so if you need to use cookies for things like authentication then you will want to manage them yourself.

To store and manage cookies yourself you will need to disable Crawlera cookie handling with the :ref:`x-crawlera-cookies` header. If cookie handling is not disabled, Crawlera will discard the cookies and send its own instead.

.. _upgrading-your-account:

Upgrading Your Account
======================

You can upgrade your Crawlera account in the `Plans` section of your `Organizations's` page. You can choose to upgrade to a Shared, Dedicated or Enterprise plan. To find out more about these plans please visit the `Pricing & Plans <http://scrapinghub.com/pricing>`_ page.

.. _fetch-api:

Fetch API
=========

Crawlera's fetch API let's you request URLs as an alternative to Crawlera's proxy interface.

Fields
------

.. note:: Field values should always be encoded.

=========== ======== ========================================= ===============================
Field       Required Description                               Example
=========== ======== ========================================= ===============================
url         yes      URL to fetch                              `http://www.food.com/`
headers     no       Headers to send in the outgoing request   `header1:value1;header2:value2`
=========== ======== ========================================= ===============================

Basic example::

    curl -u <API key>: http://proxy.crawlera.com:8010/fetch?url=https://twitter.com

Headers example::

    curl -u <API key>: 'http://proxy.crawlera.com:8010/fetch?url=http%3A//www.food.com&headers=Header1%3AVal1%3BHeader2%3AVal2'


Errors
======

When an error occurs, Crawlera sends a response containing an :ref:`x-crawlera-error` header and an error message in the body.

.. note:: These errors are internal to Crawlera and are subject to change at any time, so should not be relied on and only used for debugging.

====================== =============  ======================
X-Crawlera-Error       Response Code  Error Message
====================== =============  ======================
bad_session_id         400            Incorrect session ID
user_session_limit     400            Session limit exceeded
bad_auth               401            Unauthorized mashape request
bad_auth               407
too_many_conns         429            Too many connections*
header_auth            470            Unauthorized Crawlera header
\                      500            Unexpected error
nxdomain               502            Error looking up domain
econnrefused           502            Connection refused
econnreset             502            Connection reset
socket_closed_remotely 502            Server closed socket connection
send_failed            502            Send failed
noslaves               503            No available proxies
slavebanned            503            Website crawl ban
serverbusy             503            Server busy: too many outstanding requests
timeout                504            Timeout from upstream server
msgtimeout             504            Timeout processing HTTP stream
domain_forbidden       523            Domain forbidden. Please contact help@scrapinghub.com
bad_header             540            Bad header value for *<some_header>*
====================== =============  ======================

\* Crawlera limits the number of concurrent connections to 500 for standard users, and 5000 for enterprise users.

.. _sessions-request-limits:

Sessions and Request Limits
===========================

Sessions
--------

.. warning::

    Please be advised that the Sessions is an experimental feature and currently under development.

Sessions allow reusing the same slave for every request. Sessions expire 30 minutes after their last use and Crawlera limits the number of concurrent sessions to 100 for standard users, and 5000 for enterprise users.

Sessions are managed using the :ref:`x-crawlera-session` header. To create a new session send::

    X-Crawlera-Session: create

Crawlera will respond with the session ID in the same header::

    X-Crawlera-Session: <session ID>

From then onward, subsequent requests can be made through the same slave by sending the session ID in the request header::

    X-Crawlera-Session: <session ID>

Another way to create sessions is using the `/sessions` endpoint::

    curl -u <API key>: proxy.crawlera.com:8010/sessions -X POST

This will also return a session ID which you can pass to future requests with the `X-Crawlera-Session` header like before. This is helpful when you can't get the next request using `X-Crawlera-Session`. 

If an incorrect session ID is sent, Crawlera responds with a ``bad_session_id`` error.


.. _/sessions:

List sessions
~~~~~~~~~~~~~

Issue the endpoint :ref:`/sessions` with the ``GET`` method to list your sessions. The endpoint returns a JSON document in which each key is a session ID and the associated value is a slave.

*Example*::

    curl -u <API key>: proxy.crawlera.com:8010/sessions
    {"1836172": "<SLAVE1>", "1691272": "<SLAVE2>"}

.. _/sessions/SESSION_ID:

Delete a session
~~~~~~~~~~~~~~~~

Issue the endpoint :ref:`/sessions/SESSION_ID` with the ``DELETE`` method in order to delete a session.

*Example*::

    curl -u <API key>: proxy.crawlera.com:8010/sessions/1836172 -X DELETE

Request Limits
--------------

Crawlera’s default request limit is 5 requests per second (rps) for each website. There is a default delay of 200ms between each request and a default delay of 1 second between requests through the same slave. These delays can differ for more popular domains. If the requests per second limit is exceeded, further requests will be delayed for up to 15 minutes. Each request made after exceeding the limit will increase the request delay. If the request delay reaches the soft limit (120 seconds), then each subsequent request will contain :ref:`x-crawlera-next-request-in` header with the calculated delay as the value.


Request Headers
===============

Crawlera supports multiple HTTP headers to control its behaviour.

Not all headers are available in every plan, here is a chart of the headers available in each plan (C10, C50, etc):

============================== === === ==== ==== ==========
Header                         C10 C50 C100 C200 Enterprise
============================== === === ==== ==== ==========
:ref:`x-crawlera-ua`               ✔   ✔    ✔    ✔
:ref:`x-crawlera-no-bancheck`      ✔   ✔    ✔    ✔
:ref:`x-crawlera-cookies`      ✔   ✔   ✔    ✔    ✔
:ref:`x-crawlera-timeout`      ✔   ✔   ✔    ✔    ✔
:ref:`x-crawlera-session`      ✔   ✔   ✔    ✔    ✔
:ref:`x-crawlera-use-https`    ✔   ✔   ✔    ✔    ✔
:ref:`x-crawlera-jobid`        ✔   ✔   ✔    ✔    ✔
:ref:`x-crawlera-max-retries`  ✔   ✔   ✔    ✔    ✔
============================== === === ==== ==== ==========

.. _x-crawlera-ua:

X-Crawlera-UA
-------------
:sub:`Only available on C50, C100, C200 and Enterprise plans.`

This header controls Crawlera User-Agent behaviour. The supported values are:

* ``pass`` - pass the User-Agent as it comes on the client request
* ``crawlera`` - produce a User-Agent of type ``crawlera`` (e.g. ``Mozilla/5.0 (compatible; Crawlera/0.1; UID 10828)``)
* ``desktop`` - use a random desktop browser User-Agent
* ``mobile`` - use a random mobile browser User-Agent

If ``X-Crawlera-UA`` isn’t specified, it will default to ``crawlera``. If an unsupported value is passed in ``X-Crawlera-UA`` header, Crawlera replies with a ``540 Bad Header Value``.

More User-Agent types will be supported in the future (``chrome``, ``firefox``) and added to the list above.

.. _x-crawlera-no-bancheck:

X-Crawlera-No-Bancheck
----------------------
:sub:`Only available on C50, C100, C200 and Enterprise plans.`

This header instructs Crawlera not to check responses against its ban rules and pass any received response to the client. The presence of this header (with any value) is assumed to be a flag to disable ban checks.

*Example*::

    X-Crawlera-No-Bancheck: 1

.. _x-crawlera-cookies:

X-Crawlera-Cookies
------------------

This header allows to disable internal cookies tracking performed by Crawlera.

*Example*::

    X-Crawlera-Cookies: disable

.. _x-crawlera-session:

X-Crawlera-Session
-------------------

.. warning::

    An experimental beta feature.

This header instructs Crawlera to use sessions which will tie requests to a particular slave until it gets banned.

*Example*::

    X-Crawlera-Session: create

When ``create`` value is passed, Crawlera creates a new session an ID of which will be returned in the response header with the same name. All subsequent requests should use that returned session ID to prevent random slave switching between requests. Crawlera sessions currently have maximum lifetime of 30 minutes. See :ref:`sessions-request-limits` for information on the maximum number of sessions.

.. _x-crawlera-use-https:

X-Crawlera-Use-HTTPS
--------------------

This header forces Crawlera to retrieve the URL using HTTPS scheme instead of HTTP. For example, to fetch https://twitter.com::

    curl -x proxy.crawlera.com:8010 -U <API key>: http://twitter.com -H x-crawlera-use-https:1

.. _x-crawlera-jobid:

X-Crawlera-JobId
----------------

This header sets the job ID for the request (useful for tracking requests in the Crawlera logs).

*Example*::

    X-Crawlera-JobId: 999

.. _x-crawlera-max-retries:

X-Crawlera-Max-Retries
----------------------

This header limits the number of retries performed by Crawlera.

*Example*::

    X-Crawlera-Max-Retries: 1

Passing ``1`` in the header instructs Crawlera to do up to 1 retry. Default number of retries is 5 (which is also the allowed maximum value, the minimum being 0).

.. _x-crawlera-timeout:

X-Crawlera-Timeout
------------------

This header sets Crawlera's timeout in milliseconds for receiving a response from the target website. The timeout must be specified in milliseconds and be between 0 and 180,000. It's not possible to set the timeout higher than 180,000 milliseconds or lower than 0 milliseconds.

*Example*::

    X-Crawlera-Timeout: 40000

The example above sets the response timeout to 40,000 milliseconds. In the case of a streaming response, each chunk has 40,000 milliseconds to be received. If no response is received after 40,000 milliseconds, a 504 response will be returned.

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
CRAWLERA_URL              proxy URL (default: ``http://proxy.crawlera.com:8010``)
CRAWLERA_ENABLED          tick the checkbox to enable Crawlera
CRAWLERA_USER             Crawlera API key
CRAWLERA_PASS             Crawlera password (set as empty string if using an API key)
CRAWLERA_MAXBANS          number of bans to ignore before closing the spider (default: ``20``)
CRAWLERA_DOWNLOAD_TIMEOUT timeout for requests (default: ``1800``)
========================= ===================================================


Using Crawlera with Selenium and Polipo
=======================================

Since it's not so trivial to set up proxy authentication in Selenium, a popular option is to employ `Polipo <http://www.pps.univ-paris-diderot.fr/~jch/software/polipo/>`_ as a proxy. Update Polipo configuration file ``/etc/polipo/config`` to include Crawlera credentials (if the file is not present, copy and rename ``config.sample`` found in Polipo source folder)::

    parentProxy = "proxy.crawlera.com:8010"
    parentAuthCredentials = "<API key>:"

For password safety reasons this content is displayed as ``(hidden)`` in the Polipo `web interface manager <http://www.pps.univ-paris-diderot.fr/~jch/software/polipo/polipo.html#Web-interface>`_. The next step is to specify Polipo proxy details in the Selenium automation script, e.g. for Python and Firefox:

.. literalinclude:: _static/crawlera-selenium.py
    :language: python

Using Crawlera with CasperJS, PhantomJS and SpookyJS
====================================================

To use session-wide Crawlera proxy with PhantomJs or CasperJS provide ``--proxy=proxy.crawlera.com:8010`` and ``--proxy-auth=<API key>:`` arguments to PhantomJS (CasperJS passes these arguments to PhantomJS).

*Example*::

    casperjs|phantomjs --proxy="proxy.crawlera.com:8010" --proxy-auth="<API key>:''" yourscript.js

SpookyJS allows you to spawn multiple instances of CasperJS suites, so ``proxy`` and ``proxy-auth`` arguments should be provided when creating a Spooky object.

*Example*::

    var spooky = new Spooky({
        child: {
            proxy: 'paygo.crawlera.com:8010',
            proxy-auth: '<API key>:'
            /* ... */
        },
        /* ... */
    },

If it's preferred that Crawlera operated only on specific URLs, they should be wrapped according to :ref:`fetch-api`

*Example in CasperJS*:

.. literalinclude:: _static/crawlera-casperjs.js
    :language: javascript

Using Crawlera from Different Languages
=======================================

.. warning::

    Some HTTP client libraries including Apache HttpComponents Client and .NET don't send authentication headers by default. This can result in doubled requests so pre-emptive authentication should be enabled where this is the case.

In the following examples we'll be making HTTPS requests to https://twitter.com through Crawlera. It is assumed that Crawlera Certificate has been installed, since `CONNECT method <http://doc.scrapinghub.com/crawlera.html#working-with-https>`_ will be employed. Alternatively, HTTPS requests can be performed by passing :ref:`x-crawlera-use-https` header and re-writing URLs (replacing ``https://`` with ``http://``).

Python
------

Making use of `Requests <http://docs.python-requests.org/en/latest/>`_ HTTP Proxy Authentication:

.. literalinclude:: _static/crawlera-python-requests-httpproxyauth.py
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

Making use of `mechanize <https://github.com/sparklemotion/mechanize>`_, a Ruby library for automated web interaction:

.. literalinclude:: _static/crawlera-mechanize.rb
    :language: ruby

Node.js
-------

Making use of `request <https://github.com/request/request>`_, an HTTP client:

.. literalinclude:: _static/crawlera-request.js
    :language: javascript

Java
----

.. note:: Because of `HTTPCLIENT-1649 <https://issues.apache.org/jira/browse/HTTPCLIENT-1649>`_ you should use version 4.5 of HttpComponents Client or later.

Quoting from an example published at `The Apache HttpComponents™ <http://hc.apache.org/httpcomponents-client-ga/examples.html>`_ project website and inserting Crawlera details:

.. literalinclude:: _static/crawlera-httpc.java
    :language: java

C#
--

.. literalinclude:: _static/crawlera-proxy.cs
    :language: csharp


FAQ
===

How do I change my user-agent?
------------------------------

To change your User-Agent you will need to use the :ref:`x-crawlera-ua` header with value ``pass``. This will instruct Crawlera to use the User-Agent header you send in the request.

How do I find the slave used in a request?
------------------------------------------

Crawlera sends ``X-Crawlera-Slave`` response header containing the IP address and port of the slave used to make the request.

How do I measure Crawlera's speed for a particular domain?
----------------------------------------------------------

You can use the `crawlera-bench tool <https://github.com/scrapinghub/crawlera-tools>`_. Check the GitHub page for more information on how to use it.

Where can I monitor my Crawlera usage?
--------------------------------------

Go to your profile page in `Scrapinghub dashboard <http://dash.scrapinghub.com/>`_ and you should see your Crawlera accounts in the Crawlera section. If you click on a user, you will be able review the number of requests per day/month for that user.

Why are requests slower through Crawlera as opposed to using proxies directly?
------------------------------------------------------------------------------

If you're using your own proxies, you may notice a discrepancy in speed between using your own proxies and using them with Crawlera. This is because Crawlera throttles requests by introducing delays to avoid being banned on the target website.

These delays can differ depending on the target domain, as some popular sites have more rigorous anti-scraping measures than others. Throttling also helps prevent inadvertently bringing down the target website should it lack the resources to handle a large volume of requests.

