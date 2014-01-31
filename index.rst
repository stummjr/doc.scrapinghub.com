Scrapinghub documentation
=========================

The Basics
----------

Scrapinghub is the most advanced platform for deploying and running web
crawlers (also known as "spiders" or "scrapers"). It allows you to build
crawlers easily, deploy them instantly and scale them on demand, without having
to manage servers, backups or cron jobs. Everything is stored in a highly
available database and retrievable using an API.

Spiders can be written in `Scrapy`_ or built using the `Autoscraping`_ tool,
and they are grouped into projects. Each spider run is known as a "job".

Pay as you go plan
------------------

The Pay as you go plan runs your spiders in shared servers that are allocated
to deal with the capacity demand of all Scrapinghub users.

Dedicated servers
-----------------

In the dedicated server plan, we deploy a dedicated server where all your
spiders will run, not sharing any computing resources with other customer
spiders. At the moment, dedicated servers are powered by Amazon EC2 small
instances. More powerful severs can be requested, but it's often suggested to
allocated multiple dedicated servers instead of more powerful ones.

Here you will find reference documentation. For articles, guides and other help
resouces please visit the `Support site`_ and look for the Articles section of
each product.

Table of contents
-----------------

.. toctree::
   :maxdepth: 2

   dash
   scrapy-cloud
   api
   autoscraping
   addons

.. _Support site: http://support.scrapinghub.com
.. _Scrapy: http://scrapy.org
.. _Autoscraping: http://scrapinghub.com/autoscraping
