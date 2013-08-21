.. _dash:

========
The Dash
========

The Scrapinghub dashboard (or "Dash" for short) allows you to control and
monitor your spiders. This page contains information on how to use it.

Projects, spiders and jobs
==========================

A project consists of many spiders, that can be run. Each spider run is called
a "Job".

Jobs dashboard
==============

The jobs dashboard can be used to monitor and control your spiders.

To schedule a spider for running select the spider in the `Scheduler spider`
box and click the `Schedule` button.

Pending Jobs
------------

Pending jobs is the list of jobs that are waiting to start. Jobs may not start
immediately after scheduled, sometimes they must wait for resources to be
available.

Running Jobs
------------

Runing jobs is the list of jobs that are currently running.

Completed jobs
--------------

Completed jobs contains the latest completed jobs.

Job outcomes
============

The job outcome indicates whether the job succeeded or failed. By default, it
contains the value of the spider close reason in Scrapy.

These are the most common job outcomes and their meanings:

* ``finished`` - the job finished successfully (it may contain errors though)

* ``shutdown`` - the job was cancelled, either from the dash or due to
  Scrapinghub internal maintenance

* ``failed`` - the job failed for some reason. The most likely situation is
  that the crawling process failed to start due to a bug in the project's code.
  Check the last lines of the job log for more info.

* ``killed`` - the job was killed by Scrapinghub because it failed to respond
  or because it failed to shutdown gracefully

* ``slybot_fewitems_scraped`` - this is an autoscraping specific outcome. Please refer
  to the :doc:`autoscraping` help document.

Getting CSV exports
===================

You can get scraped data in CSV format by clicking the CSV link when browsing
items.

The fields used for the CSV can be configured in Settings -> Project Details.

.. _deploy-egg:

Deploying eggs
==============

If your project needs code from an external python library, you can deploy a python egg by using dash.
See `setuptools` for details about egg concepts, preparation and building. If you want to provide to your project your own library,
the first steps are, in short:

#. Write your code,
#. add a setup.py file in the base package folder, e.g.:

::

    from setuptools import setup, find_packages
    setup(
        name = "mylibrary",
        version = "0.1",
        packages = find_packages(),
    )

And, in order to deploy in your project:

#. Run ``python setup.py bdist_egg``,
#. In dash, go to Settings -> Eggs -> Add Egg, and fill the requested data in the upload form.

.. _setuptools: http://peak.telecommunity.com/DevCenter/setuptools
