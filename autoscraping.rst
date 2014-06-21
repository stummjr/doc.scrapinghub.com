.. _autoscraping:

============
Autoscraping
============

Autoscraping (*AS* for short) allows to visually develop a spider without programming knowledge.

.. _as-basic-concepts:

Basic Concepts and Procedures
=============================

Each time you run a new job, two processes occur: items are extracted and visited pages are captured (stored). Extracted items are what you actually want as a final result. Captured pages allow you to add templates and test extraction each time there are changes in the template set, without the need to run the spider again.

The general procedure for creating an autospider is the following one:

    #. Create and configure the spider (minimum configuration requires a name for the spider and the starting URL).
    #. Run it.
    #. Go to captured pages (see *Pages* tab of the job) and find the one which you need to scrape data from.
    #. Add and annotate a template.
    #. Go back to captured pages and check how items are extracted from them.
    #. Eventually improve current templates and/or jump to step 4.
    #. Once you feel the templates set is complete, run the job again in order to generate items.

In most cases you would need to perform some improvements in the templates, or even add new ones, because current one(s) are not suitable for extracting data from all the pages you need to extract data. This can be the case regardless of how similar the target pages look in the browser, because their HTML sources can have important differences.

The running time of jobs is limited by predefined criterias. This is a very important mechanism responsible for avoiding infinite crawling loop traps under certain conditions. The limitation is required for checking the number of items extracted each fixed period of time. If this count does not reach a given threshold, the job will be automatically closed with status ``slybot_fewitems_scraped``. See `Job Outcomes`_ for details.

You can also discover that in some cases the spider may consume a lot of time crawling pages that you don't need, thus reducing the items/pages ratio and, therefore, the spider's efficiency. By default, the Autoscraping spider extracts every link it finds, and follows it. So a very important feature in spider development is *Link* filtering. By avoiding unnecessary pages the crawling will complete in less time and the items/pages ratio will increase, reducing the risk of a premature stop of the job as a result of an unaccomplished rate threshold.

This section has been a basic (but essential) overview of the general concepts that you must know in order to better understand a more detailed description that will follow in the next sections. At the end of this document there is a list of common good practices for getting best results with less effort.

Also please take a time to watch the AS tour video in http://scrapinghub.com/autoscraping before continuing, it will be of great help.


How Templates Are Used in the Extraction Process
================================================
	
If your spider has only one template, the process is very simple: a scan is performed on every target page using the annotations in the template. If all **Required** fields are extracted in accordance with the relative positional algorithm and the extraction rules defined by the field data type (as described in the previous section), then the complete item is extracted. If some of the fields marked as **Required** are not found in the target, the item is not extracted. And if the item is extracted, it still must pass the duplicates detector check, which will decide, as aforesaid, whether the extracted item will be finally accepted or rejected.

If your spider has more than one template, the templates are tried sequentially until the first success extraction occurs. Then the duplicates detector is applied over the extracted item. The order in which templates are tried **is not** the same order in which they were created (as they appear in *Scrapinghub Dash*), but instead they are sorted by the number of annotations they contain, in the order of descending. Only if a subset of templates has the same number of annotations, they will be tried according to their age (beginning with those created first). The rule of trying templates according to the number of annotations improves the efficiency of the general extraction algorithm, as the less annotations a template has, the more likely it is to extract an item by mistake (because there are less constraints to fulfill). So, this rule reduces the probability of getting a false positive with the wrong template, and facilitates implementing the alternatives for handling such false positives (because as a template has more annotations, the user has a chance to add more constraints).


Item Fields
===========

Before adding the first spider, you must carefully design the set of item fields you will need. AS might not work at all if the design is wrong.

You can create as many different item types as you need for different spiders of your project. Item types can be added, edited, and removed in the **Items** subsection of **Settings** section. When you add a new item, it will come with *url* field already defined by default, because AS backend automatically adds it to every scraped item in a job. The specifications of this field are not editable, because it already has the necessary flags and type specifications.

The most important parameters are the type of data and two flags, *Required* and *Vary*.

Field Data Type
---------------

The type of data specifies basic extraction patterns that the corresponding target region in the selected page must fulfill in order to actually be extracted. This feature helps the main extraction algorithm to spot the correct region in the page and resolve possible ambiguities. Additionally, it contains specifications on how to render the field value in the item browser, a feature that is used also when coping with code spiders (or manual spiders *MS*, as opposed to Autoscraping spiders *AS*), which are out of the scope of this tutorial. Let's enumerate the available item types:

geopoint
  This is a special field type with no special extraction pattern, only serving for the purpose of rendering a tuple of latitude/longitude. This type is not currently supported in AS and only used for rendering purposes in MS, so you can safely ignore this one.

image
  This field type demands the spotted HTML code to fulfill the format of an image URL and renders as an image in the item browser.

number
  The spotted HTML code must have a number, and only the number part will be extracted.

price
  Similar to the number type but will also consider common formatting of price expressions in order to match.

raw html
  The HTML code as it is. It matches anything except an empty string.

safe html
  Matches any HTML code which has no empty text content. Also performs some transformation and cleaning over the extracted data in order to safely show the correctly formatted HTML code on a web page.

text
  Matches any HTML code which has no empty text content, and only extracts that text content.

url
  Matches any URL expression and renders as a link in the item browser.

Field Flags
-----------

There are two field flags that modify the behavior of extraction and item creation:

Required
  When a field is marked as **Required**, it means that an extracted item must contain the field in order to be actually extracted. We will return to this concept in the next section.

Vary
  Autoscraping has a duplicate item detection system which will reject any item that has already been scraped. In order to accomplish this task the duplicates detector needs to know which fields must be compared in order to effectively find duplicate items. If a field is marked as **Vary**, it is not included in the checks to detect duplicates. This means that two items that have the same data in all fields except those marked as **Vary**, will be considered identical and, therefore, the second scraped item will be dropped. Or, to put it another way, when you mark a field as **Vary** you are declaring that the same item may be found with different values in this field. It is for this reason that *url* field must always be marked as **Vary** (and the user interface does not allow to unselect it): if it wasn't a **Vary** field, then items from different URLs would always be considered different and the duplicates detector would never work.

  Let's illustrate with an example. Suppose we have an item type with fields *name*, *price*, *description*, *category* and *url*, while the fields *category* and *url* are marked as **Vary**. Now suppose that the Autoscraping bot has scraped the following item first:

  * *name*: Louis XIV Table
  * *price*: 1000.00
  * *description*: Very high quality Louis XIV style table
  * *category*: Tables
  * *url*: \http://www.furniture.com/tables/louis-xiv-table.html

  Then later it extracts the following item in a different place on the site:

  * *name*: Louis XIV Table
  * *price*: 1000.00
  * *description*: Very high quality Louis XIV style table
  * *category*: Living Room
  * *url*: \http://www.furniture.com/living-room/louis-xiv-table.html

  It is, of course, the same product, but the specific map of the site allows it to appear in two different places under different product categories. Because *url* and *category* are marked as **Vary**, only *name*, *price* and *description* are checked by the duplicates detector. Since all of these fields have the same value in both items, the second one is considered a duplicate of the first, and so it is rejected. Note that if *url* and *category* were not marked as **Vary**, then the duplicates detection system would consider them as different products, and so both would be generated. The term *Vary* is used to indicate that fields marked in this way may vary their values, still allowing items to be treated as identical.

Shortcut Key
------------

You can add a field shortcut key in order to quickly select a field when you are inside the annotation tool. We will return to field shortcuts in more detail in the section about the annotation tool (which is not redacted yet).


Spider Parameters
=================

When you create a new spider, the minimum set of attributes you have to fill in order to run the first crawling job are the spider name and the starting URLs (at least one). The first job for a newly created spider should be an *annotating mode* job, because the obvious first task for every new spider is to add templates. The results of the first job and all successive ones will give you a better idea about improving the crawling performance by adding URL filters. URL filters are optional, but strongly recommended in most cases, as we have explained in the section about `Basic Concepts and Procedures`_.

URL Filters
-----------

There is a filter that is always applied, and two kinds of custom filters. The former is the offsite filter that makes sure the bot cannot go beyond the bounds of the target site(s). Without this kind of filter the bot would crawl links from the entire web and avoid focusing on the target. And if there weren't other kinds of filters, it indeed would crawl all the web.

The offsite filter restricts the bot to only visit links that belong to the web domains specified in the start URLs and the template URLs (if any), everything else gets filtered out. It has precedence over any other kind of URL filter. One might wonder whether there is a redundancy in adding template URLs domains, since the templates were obtained from the crawling limited to the start URLs domains. This is true in most cases, but it also can happen that you use one start URL for creating templates and a different one for scraping items, both being from different domains.

The other two kinds of URL filters are user custom ones: **Exclude Pattern** and **Follow Patterns**, both configurable in the autospider properties (select an autospider in *Autoscraping* section and click *Edit*).

**Exclude Patterns** option allows to filter out URL patterns (regular expressions) that must not be visited. You can add as many as you want, one per line in the corresponding widget. **Exclude Patterns** has precedence over **Follow Patterns**.

You can select between 3 modes of link following:

  * **Follow all links within the spider domains** (except those defined in *Excluded Patterns*) - here the spider domains mean the ones described above: the domains specified in start URLs and template URLs 
  * **Don't follow links** - limit crawling to the start URLs only
  * **Follow links that match the following patterns** - when this mode is selected, a new text widget becomes visible where you can write the *Follow Patterns* (regular expressions) that the links have to match in order to be followed

The **Follow Patterns** are the filters with less precedence. It means that you can't force a spider to follow links on a different domain by adding it in this category of filters. The only domains that will be accepted are those contained in the start URLs and the template URLs.

Considerations When Using URL Filters
-------------------------------------

Despite the seeming simplicity of adding patterns in order to focus only on the desired targets, you must be warned about possible unexpected consequences of applying the URL filters. It is easy to fall into the trap of excluding the pages that you deemed unnecessary, just to discover later that the pages you needed are missing from a new job's results. This may happen because some excluded pages contained the links to the needed ones, thus cutting the path to them. The results depend a lot on the target site topology.

Consider the following simple example:

    #. Your start URL is ``http://www.example.com``.
    #. The start URL has a link to a product listing, let's say ``http://www.example.com/bathrooom/``.
    #. The product listing above has links to two products: ``http://www.example.com/products/1`` and ``http://www.example.com/products/2``.

If you add a filter to follow only the pattern ``/products/``, you will exclude ``http://www.example.com/bathrooom/``, therefore the links matching the pattern ``/product/`` will never be reached (unless there are products linked from the starting page, but you will probably lose most of them anyway).


Annotating a Template
=====================

The process of annotating a template consists in annotating elements on it, that is, marking elements in the template and mapping them to a given item field. At its most basical level, the Autoscraping extraction involves trying to match the annotated elements in the templates to the target pages, extract the data from the matching regions, and assign it to the field specified in the corresponding annotation. The process is repeated with all the annotations in the template, and the final item is built using all the extracted data.

The usual way to annotate an element is by clicking on it. An annotation window will pop up enabling the user to set up various options: where the data must be extracted from (the text content of an element, or some of its attributes), the field that the extracted data must be assigned to, and other options that will be described later in this section and the following ones.

Partial Annotations
-------------------

Another way to annotate a region in the template is using partial annotations. Instead of clicking on an existing element defined by the page layout, you can instead paint a piece of text with the mouse. A confirmation dialog will appear, followed by the annotation window pop-up.

Certain restrictions apply when using partial annotations. The painted region must fall inside a layout element. In other words, you cannot include in the painted region a text from more than one page element (your attempts to do so will be prevented by the annotation tool).

The tool is also intended for extracting a text inside a repetitive pattern. In order for it to work, there should be either a common prefix or a common suffix (or both) at the sides of the painted region in all the target pages. For example, if a template contains the following text inside a page element::

        Veris in temporibus sub Aprilis idibus habuit concilium Romarici montium

And the target page contains the following text in the same place::

        Cui dono lepidum novum libellum arido modo pumice expolitum?

Don't expect that if you annotate the word ``Aprilis`` in the template, you will extract something from the target. But if instead the target's text looks like this::

        Veris in temporibus sub Januarii idibus habuit concilium Romarici montium

You will extract ``Januarii`` for sure, as the rest of the text on both sides of the word is the same.

Partial annotations are useful for extracting patterns like a significant part of the string ``item #: 27624Mb``. If you expect that ``item #: <rest of string>`` pattern will always appear in the same place, you may paint and annotate ``<rest of string>`` pattern, and the ``item #:`` part in the target will be forced to match as a part of the context. But only the text that corresponds to the painted region will be extracted.

Variants
--------

One of the options available in the annotation window when clicking on a page element is a variant you want the annotation to be assigned to. By default, the variant used is ``Base (0)``, which means that the extracted data of the annotation is to be assigned to the base item. If all annotations are assigned to the base item, then a single plain item will be generated on extraction.

But consider the situation when your item is a product with different possible sizes presented on the product page as a table, e.g.:

+---------+------+
| Single  | $300 |
+---------+------+
| Double  | $500 |
+---------+------+
|  Queen  | $650 |
+---------+------+
|  King   | $800 |
+---------+------+

And the rest of the data you want to extract is found inside a common unique element (like the name of the product, the description, or the company). In this case you should annotate the common data as a base item, and then annotate the table using variants. Usually it is enough to annotate only the first and the last row of the table (the algorithm will infer about the rest in between), so you can annotate the **Single** cell as variant 1 size, the **$300** cell as variant 1 price, the **King** cell as variant 2 size, and the **$800** cell as variant 2 price. The resulting extracted data will be assigned to the base item's special field ``variants``, which is a list of objects similar to an item. An example of an item extracted in this way could be::

    {'name': 'Louis XV Bed',
     'description': 'Very cool bed for anyone',
     'company': 'Potter Beds Inc.',
     'variants': [{'size': 'Single', 'price': '300'},
                  {'size': 'Double', 'price': '500'},
                  {'size': 'Queen', 'price': '650'},
                  {'size': 'King', 'price': '800'}]
    }

Of course, it is viable to include a post processor in the project (see `Extending the Autoscraping Bot`_) that will split an item with variants into separate items. This can be very useful, for example, when you have a page with a list of items. In this case, you would assign all annotations to a variant, and during the extraction you will get an item with a single field ``variants``, which in turn is a list of all the items on the page. A variant-splitting post processor will separate them into different items.


Advanced Tools
==============

The tools and procedures described until now are enough in order to solve most cases. However, there are instances when we don't get the expected results. Among the most common problems we may encounter are annotations that extract a wrong region on some targets, templates that are not used for the target pages we expected, or data extracted from irrelevant pages. The main source of these problems is the fact that HTML layout can contain many variations and similarities across different target pages, which can introduce ambiguities for the extraction algorithm. Also, as we can have multiple templates for the same spider, all of them intended to be used for different subsets of target pages, sometimes it is quite tricky to make the correct template to be applied to the correct target (remember `How Templates Are Used in the Extraction Process`_). In order to assist with the resolution of these problems, certain extra constraints have to be imposed on the template annotations.


Extra Requirements for Annotations
==================================

Example 1
---------

Consider the following case. We have:

  * item types which include *name*, *price*, *description* and *manufacturer*, where *name* and *price* are required fields
  * a template with annotations for all 4 of them

The result of the extraction: captured pages contain many items correctly scraped (from target set A), and many others (from target set B) which have no manufacturer, but, owing to their particular layouts, the algorithm matched the items description with the *manufacturer* annotation, while the field *description* was not extracted at all because its annotation did not match any similar region in the target. We can illustrate the situation visually as follows:

Layout A: 

+------------+-----------+
|    name    |  -price-  |
+------------+-----------+
|      manufacturer      |
+------------------------+
|      description       |
+------------------------+

Layout B:

+------------+-----------+
|    name    |  -price-  |
+------------+-----------+
|      description       |
+------------------------+

In a related move, we add a new template to one of the pages of the target set B, and annotate *name*, *price* and *description*. We would expect that 
by adding this new template, the problem will be fixed. But this is not the case because the first template has more annotations than the second, so it will be tried first. And since all required fields (*name* and *price*) will be extracted, the item will still be created with the wrong data because the second template will never be applied.

We have to add a new constraint by opening the first template in the annotation tool and marking the *description* annotation as required. With this in place the items of target set B will not be created by the first template because *description* field will not be extracted with it. So the algorithm will try the second template, which now will correctly extract three fields.

Note that the template with three annotations could be tried first if the templates were not tried in decreasing order of annotations quantity. As a result we could get erroneously extracted data from the pages of target set A. In particular, we would most probably get the manufacturer data in *description* field while missing the actual description. But in this case, if there is no other way to differentiate between description and manufacturer data, it is not possible to apply any constraint. The first approach would be to constrain the application of the template with four annotations by requiring to extract the missing field, because from target set A we extract four fields, and from target set B we extract three. The second approach would be to allow the first tried template (the one with three annotations) extract three fields for both target sets.

As previously stated, the more annotations we have, the more constraints we can add.

Example 2
---------

The less required fields you have, the less constraints you are imposing, and, as a result, the easier it is to match wrong targets. As the previous example showed, not only we can match desired targets with a wrong template, but we can also match undesired targets which have layouts similar to one or more templates. When faced with such problem, one possible approach is to check whether we can mark certain annotations as required in those problematic templates. In particular, we should focus on the annotations which are not extracted from the undesired targets, and which do not affect the extraction of desired ones (but still can have those as optional attributes), thus avoiding the creation of items for them.

It's not the only approach to try in this instance though. It may be possible to filter out those undesired pages with excluded URLs while not affecting the crawling of the site (as mentioned before, those pages could contain the links to desired pages). This is the most preferable approach in terms of efficiency gain, but it's not always feasible. It depends entirely on the site particularities and your needs.


Sticky Annotations
==================

Another instrument for solving certain problems is the use of *sticky annotations*, available in the annotation tool as *_stickyN* (N being a number) together with the field names. Sticky annotations can be used for creating additional annotations without generating additional extracted data. For example, when you are extracting undesired targets with some of the templates, and you don't have the choice to filter by URL or mark certain annotations as required, you can still add new annotations in the template to match particular features of the desired targets that do not exist in the undesired ones: a particular logo, an image, a button, a piece of text, etc.

Sticky annotations are assigned implicitly, and can be added as many as needed. It's to be recalled that adding more annotations to a template increases its precedence level in the template try sequence.


Template Extractors
===================

Consider the following situation. We have a set of target pages containing user profiles, in turn consisting of tabulated data of the same type -- *name*, *gender*, *occupation*, *country*, *favorite books* and *favorite movies*:

+--------------+-------------------+
|      Name:   |       Olive       |
+--------------+-------------------+
|    Gender:   |      Female       |
+--------------+-------------------+
|  Occupation: |     FBI Agent     |
+--------------+-------------------+
|   Country:   |       USA         |
+--------------+-------------------+
|  Fav.Books:  | The First People  |
+--------------+-------------------+
|  Fav.Movies: |    Casablanca     |
+--------------+-------------------+

Fields are not required to be filled out in all user profiles, except the page we have chosen for our template. This condition will make a positional matching on its own to fail, and we will obtain mixed data as a result. For example, if a user did not provide *occupation* and *country*, we would get the favorite books in the *occupation* field, the favorite movies in the *country* field, and nothing in the fields *favorite books* and *favorite movies*. We can't mark as required any of the annotations because actually all of them are optional (besides, it would not solve the positional problem anyway).

Here the template extractors come to help, by adding pattern constraints to the template annotations. First, we annotate the entire field row ("Name: Olive", "Gender: Female", etc.) instead of the field value cell ("Olive", "Female", etc.). Then, in the template properties, we add *Regular Expression* extractors for each field in the following form:

+--------------+--------------------+--------------------+
|  Field name  |        Type        |    Specification   |
+==============+====================+====================+
|    *name*    | Regular expression |    Name:\\s+(.*)   |
+--------------+--------------------+--------------------+
|   *gender*   | Regular expression |   Gender:\\s+(.*)  |
+--------------+--------------------+--------------------+
| *occupation* | Regular expression | Occupation:\\s+(.*)|
+--------------+--------------------+--------------------+
|     ...      |        ...         |        ...         |
+--------------+--------------------+--------------------+

And so on.

When you choose a *Regular Expression* extractor, the specification must contain a regular expression pattern that must match the extracted data for the corresponding field. If the extracted data does not match the pattern, then the field is not extracted. If the extracted data does match the pattern, then it is replaced by the match group enclosed between parentheses (or a concatenation of all of them, if more than one group given). This way, you will ensure that correct annotation matches the correct target row, and you will only extract the part that you are interested in.

Of course, this method will only be useful if you can annotate a region that has a certain key word or a repeated pattern, and all of them differ for each field.


Job Outcomes
============

Apart from generic job outcomes that indicate a reason of a job termination (see :doc:`dash`), there is an Autoscraping specific outcome, ``slybot_fewitems_scraped``.

AS spiders have a safety measure to avoid infinite crawling loops: if the number of scraped items did not reach a minimum threshold over a given period of time, the job is closed. By default, the period is 3600 seconds and the minimum number of items scraped during this period must be 200. Both values are controlled by the settings ``SLYCLOSE_SPIDER_CHECK_PERIOD`` (seconds) and ``SLYCLOSE_SPIDER_PERIOD_ITEMS`` (minimum number of items scraped during the defined period).

If you are crawling a big site with thousands of pages, of which only a small portion generates items with current templates, the bot can consume long periods of time crawling while scraping only a few items. Another reason that leads to the same situation is that the bot spends a lot of time scraping duplicated products
(see *Vary* flag in `Field Flags`_ section) which are dropped instead of issued, and so they don't count for the minimum threshold of items. In both cases the spider may unexpectedly stop with ``slybot_fewitems_scraped`` outcome.

The solution depends on what exactly happens. In order to diagnose the problem, the first move would be to switch the ``LOG_LEVEL`` setting for the spider to ``DEBUG``, and start a new job (select the spider in *Spiders* section, click *Settings* tab (next to *Details*), click ``+`` button under *Project Settings* to add a new entry and choose ``LOG_LEVEL`` from the list of options). This way the bot will generate a lot of debugging data that you can browse in the job log. In ``DEBUG`` log level you will see, among other information, a line for each crawled page and each dropped product, enabling you to decide whether it's worth adding more templates and URL filters to avoid unneeded pages during the crawling (URL filters must be designed with care so as not to unintentionally block pages leading to the pages you want).


Extending the Autoscraping Bot
==============================

The autoscraping method is limited by its nature. Sometimes there's a need to do custom operations that are beyond the scope of AS core, tasks that can be performed by extending the bot's capabilities in some fashion and reduced to a post-processing task.

Scrapinghub provides *Addons*, standard components for performing common tasks, which can be enabled and configured from *Scrapinghub Dash*. Many of them are generic for any project, but others are considered as autoscraping-specific. See :doc:`addons` documentation for more information.

Another way to extend an autoscraping project with custom post-processing is by deploying a custom *Scrapy* project with extensions, middlewares and settings written for your specific needs. Since a Scrapy project may contain both autoscraping spiders and your custom coded ones, you will need a way to separate their settings. For this purpose you can resort to environment variables set up by Scrapinghub backend. The most common structure of a project ``setting.py`` file that separates the configuration for the autoscraping spiders is as follows::

    import os

    ...
    <common settings>
    ...

    SHUB_JOB_TAGS = os.environ.get('SHUB_JOB_TAGS')
    SHUB_SPIDER_TYPE = os.environ.get('SHUB_SPIDER_TYPE')

    if SHUB_SPIDER_TYPE == 'auto':
        <import/set autoscraping settings>
    else:
        <import/set not-autoscraping project settings>

The environment variable ``SHUB_SPIDER_TYPE`` will be set to *auto* by Scrapinghub backend if the spider that loads the basic settings module is an
autoscraping spider.


Autoscraping and ScrapingHub API
================================

If you want to manage AS job scheduling with the use of ScrapingHub :ref:`schedule-api`, AS spiders support additional parameters in order to override the spider
properties per job. For instance, you may want to set a list of start URLs for a specific job, or scrape only one specific URL. You can pass ``start_urls`` as a list of URLs separated by new lines. This feature is very useful for passing a list of URLs from a text file, one URL per line. Example::

    curl https://dash.scrapinghub.com/api/schedule.json -d project=155 -d spider=myspider -u <your api key>: -d start_urls="$(cat start_urls.txt)"

or, using `Scrapinghub Python API <https://github.com/scrapinghub/python-scrapinghub>`_::

    >>> from scrapinghub import Connection
    >>> conn = Connection('<your api key>')
    >>> project = conn["155"]
    >>> project.schedule("myspider", start_urls=open("start_urls.txt").read())

In the same way you can override per job specifications like ``follow_patterns`` (a list of regular expressions that links must match in order to be followed), ``exclude_pattern`` (exclude links that match them) and ``allowed_domains`` (a list of extra domains to be accepted).

Another overridable parameter is ``links_to_follow``. This parameter governs whether or not to follow links, and can take two values: ``none`` or ``patterns``. The
first value disables the link extraction, the second one enables it (thus applying follow and exclude patterns, if given). Overriding this parameter can be useful, for example, when your spider is run periodically to crawl an entire site (thus, it follows links), but you want also to trigger jobs for updating specific items. So, if you want to scrape a single item, let's say, ``http://example.com/myproduct``, you could do::

    curl https://dash.scrapinghub.com/api/schedule.json -d project=155 -d spider=myspider -u <your api key>: -d start_urls=http://example.com/myproduct -d links_to_follow=none

Or, using `Scrapinghub Python API <https://github.com/scrapinghub/python-scrapinghub>`_::

    >>> project.schedule("myspider", start_urls="http://example.com/myproduct", links_to_follow="none")

For specific Autoscraping API calls, check :ref:`autoscraping-api`.


Good Practices for Best Results with Less Effort
================================================

Autoscraping is an advanced set of tools which for some cases requires a bit of practice and experience in order to avoid common mistakes and get the best results faster. Every resource is thoroughly described in the previous sections. Nevertheless we provide a recap below in order to summarize important tips that you should bear in mind when developing autoscraping spiders as it should improve the learning curve:

1. **When defining the item fields, be sure to mark as required only those fields that you expect to be present in all items of that class.** Required fields are of great importance in governing the templates not to extract data from wrong targets, but if you don't annotate a required field in a given template, then the template will not extract anything.

2. **Don't assume that one template is enough for extracting every product you need.** Usually there are certain differences between target HTML layouts (although not visibly evident when rendered in a browser) that make some templates not a perfect fit for some targets.

3. **The captured pages browser allows you to test how the extraction behaves at any time with the current set of templates, without the need of running additional jobs.** Each time you add a new template or modify an existing one, the extracted data is updated according to the new state of templates after you have reloaded the list of captured pages.

4. **When a target is not extracted by current set of templates, remember the development cycle described in the first section.** Begin with identifying a target page that does not contain extracted data, then add and annotate a new template from it. Afterwards re-check the set of captured pages and ascertain whether there are still product pages with no data extracted which require additional templates. Once you are satisfied with the current templates set, run a new job in order to generate the items.

5. **You may encounter a contrary case as well, getting data extracted from irrelevant pages, or using an incorrect template for certain product pages.** Both cases may be solved by including additional required fields in the given template -- in particular, the fields that are not being extracted by it. As a result, the template will be discarded, since not all required fields will be extracted using it.

6. **Check for URL patterns which can be *safely* filtered out using *follow* or *excluded* regular expression patterns.** We emphasize *safely* here so you would make sure there's no risk of blocking desired pages when using such URL filters. That said, the method greatly improves the performance in many cases allowing the bot not to waste time visiting unnecessary pages.

7. **When there's a need to identify problems (e.g. to check the items dropped by the duplicates detector), use the setting LOG_LEVEL = DEBUG for getting extra information in the logs.** It will help you to elaborate better URL filters in complex cases.

8. **The** :ref:`querycleaner` **addon also helps a lot in URL filtering**. It's quite common to have certain URL parameters removed from the URL without changing the results, which makes the bot waste time visiting the same pages repeatedly, because each time they are visited with a different set of parameters. Such condition is usually indicated by a large number of dropped duplicated items.

Additional articles on the subject of best practices and improving performance can be found at `Autoscraping support forum <http://support.scrapinghub.com/list/19086-general/?category=4878>`_.

.. _autoscraping-api:

Autoscraping API
================

as/project-slybot.zip
---------------------

Retrieves the project specifications in slybot format, zip compressed. By default includes the specification of all the spiders in the project.

* Supported Request Methods: ``GET``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``spider`` *(optional and multiple)* - if present, include only the specifications of given spiders

*Examples:*

To download the entire project with ID ``123`` (including all spiders)::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/as/project-slybot.zip?project=123"

To download only the spider with name ``myspider``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/as/project-slybot.zip?project=123&spider=myspider"

as/spider-properties.json
-------------------------

Retrieves or updates an autoscraping spider properties. If no update parameters are given, the call returns the current properties of the spider.

1. Retrieves an autoscraping spider properties.

* Supported Methods: ``GET``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``spider`` *(required)* - the spider name

2. Updates an autoscraping spider properties.

* Supported Methods: ``POST``

* Parameters:

  * ``project`` *(required)* - the project's numeric ID
  * ``spider`` *(required)* - the spider name
  * ``start_url`` *(optional and multiple)* - set the start URL and update ``start_urls`` property with the given values

*Examples:*

To get the properties of the spider ``myspider``::

    curl -u APIKEY: "https://dash.scrapinghub.com/api/as/spider-properties.json?project=123&spider=myspider"

To update the start URLs of a spider::

    curl -u APIKEY: -d project=123 -d spider=myspider \
            -d start_url=http://www.example.com/listA \
            -d start_url=http://www.example.com/listB \
            https://dash.scrapinghub.com/api/as/spider-properties.json


