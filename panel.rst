===============
Using the Panel
===============

The Scrapinghub panel allows you to control and monitor your spiders. This page
contains information on how to use it.

Projects, spiders and jobs
==========================

A project consists of many spiders, that can be run. Each spider run is called
a "Job".

Jobs dashboard
==============

The jobs dashboard can be used to monitor and control your spiders.

The schedule a spider for running select the spider in the `Scheduler spider`
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

* ``shutdown`` - the job was cancelled, either from the panel or due to
  Scrapinghub internal maintenance

* ``failed`` - the job failed for some reason. The most likely situation is
  that the crawling process failed to start due to a bug in the project's code.
  Check the last lines of the job log for more info.

* ``killed`` - the job was killed by Scrapinghub because it failed to respond
  or because it failed to shutdown gracefully

Getting CSV exports
===================

You can get scraped data in CSV format by clicking the CSV link when browsing
items.

The fields used for the CSV can be configured in Settings -> Project Details.
