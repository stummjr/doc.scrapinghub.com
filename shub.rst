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

Here are the available commands:

.. BEGIN_SHUB_USAGE - DO NOT EDIT MANUALLY THIS BLOCK
::

    $ shub --help
    Usage: shub [OPTIONS] COMMAND [ARGS]...
    
      Scrapinghub command-line client
    
    Options:
      --help  Show this message and exit.
    
    Commands:
      deploy       Deploy Scrapy project to Scrapy Cloud
      deploy-egg   Build and deploy egg from source
      deploy-reqs  Build and deploy eggs from requirements.txt
      fetch-eggs   Download a project's eggs from the Scrapy...
      login        add Scrapinghug API key into the netrc file
.. END_SHUB_USAGE

To see the usage for each command, use::

    shub <COMMAND> --help

Configuration
=============

Configuration is currently read from the Scrapy project's ``scrapy.cfg`` file as well as the home ``~/.scrapy.cfg`` file, and it's compatible with `scrapyd-deploy`_ command.

.. _scrapyd-deploy: http://scrapyd.readthedocs.org/en/latest/deploy.html
