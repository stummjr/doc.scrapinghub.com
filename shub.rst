.. _shub:

===================
Command Line Client
===================

The Scrapinghub command line client is called ``shub`` and it can be used to interact with all Scrapinghub services. For now, it only supports deploying Scrapy projects to Scrapy Cloud but it will soon support other services.

Installation
============

To install ``shub`` use::

    pip install shub

Usage
=====

To see available commands just type::

    shub

Configuration
=============

Configuration is currently read from the Scrapy project's ``scrapy.cfg`` file as well as the home ``~/.scrapy.cfg`` file, and it's compatible with `scrapyd-deploy`_ command.

.. _scrapyd-deploy: http://scrapyd.readthedocs.org/en/latest/deploy.html
