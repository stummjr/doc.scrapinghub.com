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
      logout       remove Scrapinghug API key from the netrc...
      version      Show shub version
.. END_SHUB_USAGE

To see the usage for each command, use::

    shub <COMMAND> --help


Commands
========

.. _login-:

login
------

In order to execute/use shub commands you will first need to login::

    $ shub login
    Insert your Scrapinghub API key : d267ffd3aaefe48b694fe231bf70fb400
    Success.

.. note:: Non logged-in users would get this message::
	
	Error: Not logged in. Please login first with: shub login

Under the hood, this command creates or amends a .netrc file with our provided credentials/API-key

.. _logout-:

logout
------

To logout and, in fact, remove Scrapinghug API key from the netrc::

    $ shub logout

.. _deploy-:

deploy
------

To deploy our Scrapy project or eggs into Scrapy Cloud, we need to:

* Get into our project's root folder
* Check that our project's ``scrapy.cfg`` file has a deploy block like this one::
	
	[deploy]
	username = d267ffd3aaefe48b694fe231bf70fb400
	project = 63883

* Make sure that we are logged-in (see :ref:`login-`)

* We can deploy a:

  * ``project`` with an auto-generated or custom version::

	$ shub deploy
	Packing version 4836312012
	Deploying to Scrapy Cloud project "63883"
	{"status": "ok", "project": 63883, "version": "4836312012", "spiders": 1}
	Run your spiders at: https://dash.scrapinghub.com/p/63883/


	$ shub deploy --version 1.0.0
	Packing version 1.0.0
	Deploying to Scrapy Cloud project "63883"
	{"status": "ok", "project": 63883, "version": "1.0.0", "spiders": 1}
	Run your spiders at: https://dash.scrapinghub.com/p/63883/

  * ``project egg`` or build one without deploying::

	$ shub deploy --egg egg_name --version 1.0.0
	Using egg: egg_name
	Deploying to Scrapy Cloud project "63883"
	{"status": "ok", "project": 63883, "version": "1.0.0", "spiders": 1}
	Run your spiders at: https://dash.scrapinghub.com/p/63883/


	$ shub deploy --build-egg egg_name
	Writing egg to egg_name

.. _deploy-egg:

deploy-egg
----------

To deploy eggs into our Scrapy Cloud eggs library from:

* URL (Git, bazaar or mercurial repository URL)::

	$ shub deploy-egg --from-url https://github.com/scrapinghub/dateparser.git 63883
	Cloning the repository to a tmp folder...
	Building egg in: /private/var/folders/_8/jsdrmhzn31s5kg6d_bysr9jh0000gn/T/shub-deploy-egg-from-url_8O1yW/egg-tmp-clone
	Deploying dependency to Scrapy Cloud project "63883"
	{"status": "ok", "egg": {"version": "v0.2.1-master", "name": "dateparser"}}
	Deployed eggs list at: https://dash.scrapinghub.com/p/63883/eggs

* URL and specific GIT branch to checkout::

	$ shub deploy-egg --from-url https://github.com/scrapinghub/dateparser.git --git-branch py3-port 63883
	Cloning the repository to a tmp folder...
	py3-port branch was checked out
	Building egg in: /private/var/folders/_8/jsdrmhzn31s5kg6d_bysr9jh0000gn/T/shub-deploy-egg-from-urlFdgtLJ/egg-tmp-clone
	Deploying dependency to Scrapy Cloud project "63883"
	{"status": "ok", "egg": {"version": "v0.1.0-30-g48841f2-py3-port", "name": "dateparser"}}
	Deployed eggs list at: https://dash.scrapinghub.com/p/63883/eggs

* Package on PyPI::

	$ shub deploy-egg --from-pypi loginform 63883
	Fetching loginform from pypi
	Collecting loginform
	  Downloading loginform-1.0.tar.gz
	  Saved /var/folders/_8/jsdrmhzn31s5kg6d_bysr9jh000gn/T/shub-deploy-egg-from-pypiho_eig/loginform-1.0.tar.gz
	Successfully downloaded loginform
	Package fetched successfully
	Uncompressing: loginform-1.0.tar.gz
	Building egg in: /private/var/folders/_8/jsdrmhzn31s5kg6d_bysr9jh000gn/T/shub-deploy-egg-from-pypiho_eig/loginform-1.0
	Deploying dependency to Scrapy Cloud project "63883"
	{"status": "ok", "egg": {"version": "loginform-1.0", "name": "loginform"}}
	Deployed eggs list at: https://dash.scrapinghub.com/p/63883/eggs

.. _fetch-eggs:

fetch-eggs
----------

To fetch/download eggs from a project::

	$ shub fetch-eggs 63883
	Downloading eggs to eggs-63883.zip


.. _version-:

version
-------

To show ``shub`` version::

	$ shub --version
	shub, version 1.3.0

Configuration
=============

Configuration is currently read from the Scrapy project's ``scrapy.cfg`` file as well as the home ``~/.scrapy.cfg`` file, and it's compatible with `scrapyd-deploy`_ command.

.. _scrapyd-deploy: http://scrapyd.readthedocs.org/en/latest/deploy.html