.. _cloud:

============
Scrapy Cloud
============

Overview
========

The Scrapy cloud service allows you to deploy and run your Scrapy project in
the Scrapinghub cloud.

Supported libraries
===================

These are the libraries (and versions) available out of the box in the
cloud that you can use in your spiders:

* `Scrapy`_ (trunk)
* `w3lib`_ (trunk)
* `scrapely`_ (trunk)
* `lxml`_ 2.2.6
* `Beautiful Soup`_ 3.1.0
* `Python Imaging Library (PIL)`_ 1.1.7
* `NumPy`_ 1.3.0
* `pymongo`_ 1.10.1
* `MySQLdb`_ 1.2.2
* `boto`_ 1.9b
* `SimpleParse`_ 2.1.0a1
* `nltk`_ 2.0b8

If your project requires other external library, or a different version from
the one provided by default, you can upload them either using the Panel (in
Settings -> Project Eggs) or the :ref:`Eggs API <eggs-api>`. Binary/native
Python modules are not supported yet.

Deploying your project
======================

You can use Scrapinghub as a deployment target of the `scrapy deploy`_ command
(available since Scrapy 0.11). For more information see `Deploying your
project`_ in the `Scrapyd`_ documentation.

Single private projects
-----------------------

To deploy your project in Scrapinghub just follow these simple steps:

1. Select your project in the panel, and then go to Settings -> Scrapy Deploy
2. Copy the snippet and paste it into your project's ``scrapy.cfg`` file
3. Run: ``scrapy deploy``

Working on teams
----------------

If you're working on a project with a team, where more than one person can
deploy, it's recommended not to store your credentials in the main project
``scrapy.cfg`` file and instead store them in a private file in your home
folder. That file is ``~/.scrapy.cfg``, where ``~`` is refers to your home
directory.

So, assuming your Scrapinghub user name is ``john`` and your password
``secret``, this is how your ``~/.scrapy.cfg`` would look like::

    [deploy]
    username = john
    password = secret

And in your project ``scrapy.cfg`` you would only write the options relevant to
the deployment url and project identifier.

Example of project ``scrapy.cfg`` file::

    [deploy]
    url = http://panel.scrapinghub.com/api/scrapyd/
    project = 999

Frequently Asked Questions
==========================

I'm getting a pymongo.errors.InvalidDocument error - why?
---------------------------------------------------------

Scrapinghub uses BSON_ for storing the scraped data, which is an extension of
the JSON_ format. BSON_ doesn't support certain types and Scrapinghub policy
is not to do any implicit type conversions, to minimize developer surprises. If
you scrape an integer, you'll get an integer when you pull the data from the
API.

For this reason, some types will raise the ``pymongo.errors.InvalidDocument``
if they're not supported in BSON_.

To fix this, just declare a serializer for the field. Scrapy provides a
standard way to do this, see: `declaring field serializers`_.

Why is a job running slower than previous ones of the same spider?
------------------------------------------------------------------

Even when no changes to code are made, jobs can run slower depending on how
busy is the server they are assigned to run in the cloud.

This variability can be improved by purchasing dedicated servers. Check the
`Pricing page`_, and contact info@scrapinghub.com to request them.

.. _BSON: http://bsonspec.org/
.. _JSON: http://www.json.org/
.. _declaring field serializers: http://doc.scrapy.org/topics/exporters.html#declaring-a-serializer-in-the-field
.. _Pricing page: http://scrapinghub.com/pricing.html
.. _scrapy deploy: http://doc.scrapy.org/topics/commands.html#command-deploy
.. _Deploying your project: http://doc.scrapy.org/topics/scrapyd.html#deploying-your-project
.. _Scrapyd: http://doc.scrapy.org/topics/scrapyd.html
.. _Scrapy: http://scrapy.org
.. _w3lib: https://github.com/scrapy/w3lib
.. _lxml: http://lxml.de/
.. _Python Imaging Library (PIL): http://www.pythonware.com/products/pil/
.. _Beautiful Soup: http://www.crummy.com/software/BeautifulSoup/
.. _scrapely: https://github.com/scrapy/scrapely
.. _pymongo: http://api.mongodb.org/python/
.. _MySQLdb: http://mysql-python.sourceforge.net/MySQLdb.html
.. _boto: https://github.com/boto/boto
.. _NumPy: http://numpy.scipy.org/
.. _SimpleParse: http://simpleparse.sourceforge.net/
.. _nltk: http://www.nltk.org/
.. _declaring field serializers: http://doc.scrapy.org/topics/exporters.html#declaring-a-serializer-in-the-field
.. _Pricing page: http://scrapinghub.com/pricing.html
