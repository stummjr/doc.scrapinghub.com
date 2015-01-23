.. _crawlera:

========
Crawlera
========

First Steps
===========

Testing your credentials
------------------------

.. code-block:: text

    curl -vx paygo.crawlera.com:8010 -U <user>:<pass> http://crawlera.com

Using Crawlera with Scrapy
--------------------------

Crawlera provides a HTTP proxy interface, making it possible to use Crawlera with any software that supports the use of standard HTTP proxies. Scrapy supports the use of HTTP proxies through the http_proxy environment variable, so it’s possible to use Crawlera this way:
export http_proxy=http://user:pass@paygo.crawlera.com:8010

You can also use the Crawlera middleware provided by the scrapylib library. If you have scrapylib installed you can enable the middleware by adding the following to your Scrapy project settings::

    DOWNLOADER_MIDDLEWARES = {'scrapylib.crawlera.CrawleraMiddleware': 600}
    CRAWLERA_ENABLED = True
    CRAWLERA_USER = 'your_username'
    CRAWLERA_PASS = 'your_password'

When using Crawlera with Scrapy it’s recommended to disable the AutoThrottle extension and increase the maximum number of concurrent requests.

If you are wanting to use Crawlera with Scrapy Cloud, see Using Crawlera with Scrapy Cloud section.

Working with HTTPS
------------------

The caveat of Crawlera’s HTTP proxy interface is that is doesn’t support the CONNECT method, so HTTPS URLs won’t work with a standard proxy configuration. While some HTTP clients support HTTPS requests through standard HTTP proxies, you will most likely need to use the fetch API::

    curl -u user:pass http://paygo.crawlera.com/fetch?url=https://twitter.com

Note that it’s not recommended to use the fetch API with the Crawlera middleware. Another option is the X-Crawlera-Use-HTTPS header.


Errors
======

When an error occurs, Crawlera will send a response containing an X-Crawlera-Error header and an error message in the body.

Warning: These errors are internal to Crawlera and are subject to change at any time, so it’s strongly recommended you avoid relying on them for anything important:

====================== =============  ======================
X-Crawlera-Error       Response code  Error message
====================== =============  ======================
bad_session_id         400            incorrect session id
user_session_limit     400            session limit exceeded
\                      407
\                      500            Unexpected error
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
\                      523            Domain forbidden. Please contact support@scrapinghub.com
\                      540            Bad Header Value for “<header>”
====================== =============  ======================


Sessions and Request Limits
===========================

Sessions
--------

Crawlera supports the use of sessions which allow you to use the same slave for every request. Sessions have a lifetime of one hour and each user is limited to 10 concurrent sessions.

Sessions are managed using the X-Crawlera-Session header. To create a new session send the following:
X-Crawlera-Session: create

Crawlera will respond with the session ID in the same header:
X-Crawlera-Session: <session ID>

From there you can make subsequent requests through the same slave by sending the session ID in the header:
X-Crawlera-Session: <session ID>

If an incorrect session ID is sent, Crawlera will respond with a bad_session_id error.

Request Limits
--------------

Crawlera’s default request limit is 5 requests per second (rps) for each website. There is a default delay of 200ms between each request and a default delay of 12s between requests through the same slave. These delays can differ for more popular domains. If you exceed the requests per second limit then further requests will be delayed for up to 15 minutes. Each request made after exceeding the limit will increase the request delay. If the request delay reaches the software limit (120 seconds), then in each subsequent request you will receive an X-Crawlera-Next-Request-In header with the delay as the value.

Request Headers
===============

Crawlera supports a number of headers which can be used to control its behaviour.

\* Only available for Enterprise users.

** Only available for Scrapinghub staff.

X-Crawlera-NO-UA *
------------------

This header is deprecated, use :ref:`X-Crawlera-UA <x-crawlera-ua>`  instead with value ``pass``.

.. _x-crawlera-ua:

X-Crawlera-UA *
---------------

This header controls Crawlera User-Agent behaviour. The supported values are:

* ``pass`` - pass the User-Agent as it comes on the client request.
* ``crawlera`` - produce a User-Agent of type crawlera (e.g. Mozilla/5.0 (compatible; Crawlera/0.1; UID 10828)).
* ``desktop`` - use a random desktop browser User-Agent.
* ``mobile`` - use a random mobile browser User-Agent.

If ``X-Crawlera-UA`` isn’t specified it will default to ``crawlera``. If you pass an unsupported value in ``X-Crawlera-UA`` header, Crawlera will reply with a ``540 Bad Header Value``.

More User-Agent types will be supported in the future (chrome, firefox) and added to the list above.

X-Crawlera-No-Bancheck *
------------------------

Tell Crawlera not to check responses against its ban rules (see crawlera-config docs) and pass any received response to the client. The presence of this header (with any value) is assumed to be a flag to disable ban checks.

Example::

    X-Crawlera-No-Bancheck: 1


X-Crawlera-Cookies *
--------------------

Allows to disable internal cookies tracking performed by Crawlera.

Example::

    X-Crawlera-Cookies: disable


X-Crawlera-Session *
--------------------

Tells Crawlera to use sessions which will tie requests to a particular slave until it gets banned.

Example::

    X-Crawlera-Session: create

Tells Crawlera to create a new session which will be returned in response header with the same name. All subsequent requests should use that returned session ID to prevent random slave switching between requests. Crawlera sessions currently have maximum lifetime of 1 hour and each user is limited to have at maximum 10 concurrent sessions.

X-Crawlera-Use-HTTPS
--------------------

Force Crawlera to retrieve the URL using HTTPS scheme instead of HTTP. For example, to fetch https://twitter.com you can use::

    curl -x paygo.crawlera.com:8010 -U USER:PASS http://twitter.com -H x-crawlera-use-https:1

X-Crawlera-JobId
----------------

Set the job ID for the request. This is useful when you need to be able to see the job ID for each request in the Crawlera logs.

Example::

    X-Crawlera-JobId: 999

X-Crawlera-Max-Retries
----------------------

Tells Crawlera to limit number of retries to some specified value.

Example::

    X-Crawlera-Max-Retries: 1

Will tell Crawlera to do up to 1 retry. Default number of retries is 5. This number is also the maximum allowed and the minimum value which could be specified with this header is 0.

Response headers
================

X-Crawlera-Next-Request-In
--------------------------

Returned when response delay reaches soft limit (120 seconds) and contains that calculated delay value. If the user ignores this header, hard limit (1000 seconds) could be reached, after which Crawlera returns HTTP status code 503 with delay value in Retry-After header.

X-Crawlera-Debug
----------------

Allows to see some additional debug values in response headers. At the moment only request-time and ua values are supported, comma should be used as a separator, i.e.::

    X-Crawlera-Debug: request-time

or::

    X-Crawlera-Debug: request-time,ua

Option request-time forces Crawlera to output request time (in seconds) of the last request retry (i.e. the time between Crawlera sending request to a slave and the moment Crawlera receives response headers from that slave).

Example::

    X-Crawlera-Debug-Request-Time: 1.112218

With ua option one could get information about actual user agent which was used for the last request (could be useful for finding reasons behind target website redirects):
X-Crawlera-Debug-UA: Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN) AppleWebKit/533+ (KHTML, like Gecko)

X-Crawlera-Error
----------------

Response header specifying particular Crawlera error behind HTTP status codes (4xx or 5xx) returned for some error condition. The error message is sent in the response body.

Example::

    X-Crawlera-Error: user_session_limit

Warning: Returned errors are internal to Crawlera and are subject to change at any time and so should not be relied on.


Using Crawlera with Scrapy Cloud
================================

The Crawlera addon allows you to use Crawlera with your projects in Scrapy Cloud. You can enable the addon by going into Settings > Addons.

Settings
--------

========================= ===================================================
CRAWLERA_URL              Proxy URL (default: http://paygo.crawlera.com:8010)
CRAWLERA_ENABLED          Set to enable Crawlera
CRAWLERA_USER             Crawlera username
CRAWLERA_PASS             Crawlera password
CRAWLERA_MAXBANS          Number of bans to ignore before closing spider (default: 20)
CRAWLERA_DOWNLOAD_TIMEOUT Timeout for requests (default: 1800)
========================= ===================================================

FAQ
===

How do I add my own proxies to Crawlera?
----------------------------------------

You can send a list of proxies (hosts/IPs) along with your Crawlera username to crawlera@scrapinghub.com, or you can create a private ticket in UserEcho.

How do I change my user-agent?
------------------------------

To change your user-agent you will need to use the X-Crawlera-UA header with value ‘pass’. This will instruct Crawlera to use the User-Agent header you send in the request.

How do I find the slave used in a request?
------------------------------------------

Crawlera sends an X-Crawlera-Slave header containing the IP address and port of the slave used to make the request.

How do I measure Crawlera’s speed for a particular domain?
----------------------------------------------------------
You can use the crawlera-bench tool which is available in the crawlera-tools repository.

Where can I monitor my Crawlera usage?
--------------------------------------

On the main page, under ‘Crawlera’ you will see any Crawlera accounts linked to your Dash user. If you click on a user you will be able see the number of requests per day/month for that user.
