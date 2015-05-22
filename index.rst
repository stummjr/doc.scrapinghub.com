Scrapinghub Documentation
=========================

Scrapinghub is the most advanced platform for deploying and running web crawlers (also known as *spiders* or *scrapers*). It allows you to build crawlers easily, deploy them instantly and scale them on demand, without having to manage servers, backups or cron jobs. Everything is stored in a highly available database and retrievable using an API.

Spiders can be written in Python using `Scrapy`_, built visually using `Portia`_ tool or both. Spiders are grouped into projects. Each spider run is known as a *job*.

Here you will find reference documentation. For more articles, guides and other help resources please visit our `Knowledge Base`_ in our `Support center`_.

Products
--------

* :doc:`scrapy-cloud`
* :doc:`crawlera`
* :doc:`portia`

Platform Concepts, Features & Tools
-----------------------------------

* :doc:`dash`
* :doc:`jobs`
* :doc:`jobdata`
* :doc:`shub`
* :doc:`comments`
* :doc:`reports`
* :doc:`activity`
* :doc:`addons`

.. _api:

API
---

.. toctree::
   :maxdepth: 2

* :ref:`api-basics`
* :ref:`jobs-api`
* :ref:`items-api`
* :ref:`logs-api`
* :ref:`requests-api`
* :ref:`collections-api`
* :ref:`reports-api`
* :ref:`activity-api`
* :ref:`eggs-api`
* :ref:`frontier-api`

.. toctree::
   :hidden:

   api

   scrapy-cloud
   crawlera
   portia

   dash
   jobs
   jobdata
   shub
   comments
   reports
   addons

   activity
   collections
   frontier


.. _Support center: http://support.scrapinghub.com
.. _Knowledge Base: http://support.scrapinghub.com/forum/24895-knowledge-base/
.. _Scrapy: http://scrapy.org
.. _Portia: http://scrapinghub.com/portia/
