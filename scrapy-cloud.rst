============
Scrapy Cloud
============

*Scrapy Cloud* is a Scrapinghub service to deploy and run `Scrapy`_ spiders and
:ref:`custom python scripts <sc-scripts>`.

Plans & Pricing
===============

Scrapy Cloud has four types of plans available:

* The *Free* plan is completely free and allows you to add a single project.
* The *Shared* plan offers unlimited projects and allows you to run several crawls concurrently.
* *Dedicated* plans provide a dedicated server for running your spiders with more resources. Dedicated plans can run a larger number of concurrent crawls and have longer data retention periods.
* An *Enterprise* plan is a custom plan tailored to your needs.

Check the `Scrapinghub Pricing`_ page for more details.

Creating a Scrapy Spider
========================

Let's start by creating our Scrapy spider. If you already have a Scrapy project, you can skip this section.

This assumes you have Scrapy already installed, otherwise please refer to the `Scrapy installation guide`_.

For this example, we will build a spider to scrape the *CrunchBase* directory. For simplicity sake, we are going to restrict it to the *Pets* category:

    https://www.crunchbase.com/companies?q=pets

We begin by creating a Scrapy project which we will call ``companies``::

    $ scrapy startproject companies

Then we create a spider for ``crunchbase.com``::

    $ scrapy genspider crunchbase crunchbase.com -t basic
    Created spider 'crunchbase' using template 'basic' in module:
      companies.spiders.crunchbase

Then we edit the spider::

    $ scrapy edit crunchbase

Here is the code (for simplicity we are defining the item in the same file):

.. literalinclude:: _static/sc-crunchbase.py

For more information about Scrapy please refer to the `Scrapy documentation`_.

.. _deploy:

Deploying a Scrapy Spider
=========================

.. note:: You will need the :ref:`Scrapinghub command line client <shub>` to deploy projects to Scrapy Cloud, so install it if you haven't done it yet.

The next step is to edit ``scrapy.cfg`` file of your project and configure Scrapinghub as deployment target::

    [settings]
    default = companies.settings

    [deploy]
    project = PROJECT_ID

``PROJECT_ID`` is the numeric project ID which you can find in Scrapinghub URL:

    https://dash.scrapinghub.com/p/PROJECT_ID/...

Then you should put your API key (which you can get from your `Account page`_)
in ``~/.scrapy.cfg`` to authenticate::

    [deploy]
    username = APIKEY

Finally, you deploy your spider to Scrapinghub with the following command::

    $ shub deploy
    Server response (200):
    {"status": "ok", "project": PROJECT_ID, "version": "1391115259", "spiders": 1}

Now you should go to your project page and schedule the spider to run:

.. image:: _static/sc-schedule.png
   :width: 300px

|

Once the job has finished, or while it's running, you can click on the job to review the scraped data and other information about the job:

.. image:: _static/sc-items-scraped.png
   :width: 500px

|

Dependencies and External Libraries
===================================

The Scrapy Cloud platform comes with some libraries pre-installed that you can
use in your code without uploading an egg. `This support article
<http://support.scrapinghub.com/topic/205467-supported-libraries-on-scrapy-cloud/>`_
describes exactly which library versions are availble, and you can also
subscribe to get notified of changes to such libraries.

If your project needs code from an external Python library, you need to upload a Python egg of that dependency.

See `setuptools`_ for details about egg concepts, preparation and building. If you want to provide your own library for your project, proceed as described below:

#. Write your code.
#. Add a ``setup.py`` file in the base package folder, e.g.::

    from setuptools import setup, find_packages
    setup(
        name = "mylibrary",
        version = "0.1",
        packages = find_packages(),
    )

#. Run ``python setup.py bdist_egg`` to deploy in your project.
#. In Scrapinghub, go to **Settings -> Eggs -> Add Egg**, and fill the requested data in the upload form.

.. _sc-scripts:

Running custom python scripts
=============================

You can also run custom python scripts (ie. non Scrapy spiders) on Scrapy
Cloud. They need to be declared in the ``scripts`` section of your project
``setup.py`` file.

.. note:: Keep in mind that the project deployed needs to be a Scrapy project. This is a limitation for now, which will be removed in the future.

Here is a ``setup.py`` example for a project that ships a ``hello.py`` script::

    from setuptools import setup, find_packages

    setup(
        name         = 'myproject',
        version      = '1.0',
        packages     = find_packages(),
        scripts      = ['bin/hello.py'],
        entry_points = {'scrapy': ['settings = myproject.settings']},
    )

After you :ref:`deploy <deploy>` your project, you will see the ``hello.py``
script right below the list of spiders, in the schedule box (on Scrapinghub
dashboard).

You can also setup periodic jobs to run the script or do it via the API.

For running the script through the API, you need to use a private API (which may subject to change in the future). Here is an example using ``curl``::

    curl -X POST -d '{"job_cmd": ["py:hello.py"]}' https://storage.scrapinghub.com/jobq/1/push


.. _eggs-api:

Eggs API
========

These API calls provide a means for uploading Python eggs (typically used for managing external dependencies) to a project.

eggs/add.json
-------------

Adds a Python egg to a project.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``name`` *(required)* - the egg name
  * ``version`` *(required)* - the egg version
  * ``egg`` *(required)* - the egg to add (a file upload)

*Example:*

To add ``somelib`` egg to project ``123``::

    curl -u APIKEY: https://dash.scrapinghub.com/api/eggs/add.json -F project=123 -F name=somelib -F version=1.0 -F egg=@somelib-1.0.py2.6.egg

eggs/delete.json
----------------

Deletes a Python egg from a project.

* Supported Request Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``name`` *(required)* - the egg name

*Example:*

To delete ``somelib`` egg from project ``123``::

    curl -u APIKEY: https://dash.scrapinghub.com/api/eggs/delete.json -d project=123 -d name=somelib

eggs/list.json
--------------

Lists the eggs contained in a project.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID

*Example:*

To list all eggs in project ``123``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/eggs/list.json?project=123"


.. _Scrapy: http://scrapy.org
.. _Scrapy installation guide: http://doc.scrapy.org/en/latest/intro/install.html
.. _account page: https://dash.scrapinghub.com/account/
.. _Scrapy documentation: http://doc.scrapy.org/
.. _setuptools: http://peak.telecommunity.com/DevCenter/setuptools
.. _Scrapinghub Pricing: http://scrapinghub.com/pricing/
