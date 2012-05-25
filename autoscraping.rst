.. _autoscraping:

============
Autoscraping
============

Autoscraping (AS) allow to visually develop a spider without programming knowledge.


Basic concepts and procedures
=============================

When working with an AS spider, there are two kinds of job modes: annotating mode and normal mode. Annotating mode
allow to add templates and test them before actually running a normal mode job, which is a job that
will actually generate the items. In near future this model will be simplified for better usability,
and there will be only one running mode. But general concepts and procedures for creating a spider
will be the same.

Right now the best case procedure, in short, is:

    #. Create the spider and configure (minimal configuration is the name of the spider and the starting url)
    #. Run in annotating mode
    #. Go to captured pages and find one from which you need scraped data
    #. Add a template, and annotate it
    #. Go back to captured pages and check how items are extracted from them
    #. Run in normal mode and get the items

But usually things are not so easy. In many cases you need to perform some improvements in the template,
or even add new ones, because current one/s are not suitable for extracting data from all the pages you need
to extract data. And even this can be the case regardless how similar the target pages looks in the browser,
because the html source can have important differences. So, a less better case would be:

    1. Create the spider and configure (minimal configuration is the name of the spider and the starting url)
    2. Run in annotating mode
    3. Go to captured pages and find one from which you need scraped data
    4. Add a template, and annotate it
    5. Go back to captured pages and check how items are extracted from them
    6. Eventually improve current template and/or jump to step 4.
    7. Run in normal mode and get the items

In most cases you don't need to run a new annotating job, as the captured pages in this mode are reusable. Each time
you add a template or modify one, these changes will be reflected in extracted items after you reload the captured
pages. But there are situations when you may need to run a new annotating job.

Right now, annotating jobs are limited by number of pages
(1000 by default). This is a very important mechanism in order to avoid infinite crawling loop traps under certain conditions. The
normal jobs are also limited, but in a different way, which consist on checking the number of items extracted each hour. And if this
count does not accomplish a given threshold, then the job will be automatically stopped.

Because of this reason, usually you will get much more crawled pages in a normal job than in the annotating job, and so you can discover
that there are some items not extracted, or not correctly extracted, in a normal job, but you can't annotate it because it was not
captured in the annotating job, and so you have to try a new annotating job with spider link filters in order to avoid to capture
unnecessary pages and so leave place for capturing the important ones.

Link filters are a very important issue in spider development, not only because they allow to capture more relevant pages in annotating 
jobs, but also because they will greatly improve the performance of normal jobs, as avoiding to crawl unnecessary pages will not only 
yield to complete the site crawling in less time, but also to an increase of the item/hour rate, thus reducing the risk of unexpected 
premature stop of job as result of an unaccomplished item/hour rate threshold.

This section has been a basic, but very important, overview of general concepts that you must know in order to better understand
the detailed description that will come on following sections. Also please take a time to watch the AS tour video in 
http://scrapinghub.com/autoscraping.html before continue. Will be of great help.

Item fields
===========

Before adding the first spider, you must carefully design the item fields set you will need. A wrong design and AS will not work at all.

You can create as many different item types as you need for the different spiders of your project. Item types can be added, edited, and 
removed in the **Items** subsection of **Settings** section. When you add a new item, it will come with *url* field already defined as 
default. The url field is defined by default in the item type definition because the AS backend automatically adds it to every scraped 
item in a job. The specifications of this field are not editable, because it already has the necessary flags and type specifications.

The most important parameters are the type of data and two flags, *Required* and *Vary*.

Field data type
_______________

The type of data specifies some basic patterns that the corresponding target region in the target page must fulfill (extraction pattern) in order to actually be extracted. This feature helps the main extraction algorithm to spot the correct region in the page and resolve possible ambiguities. Additionally, it contains specifications on how to render the field value in the item browser, a feature that is used also when coping with code spiders (or manual spiders, MS, as opposition with autoscraping spiders), which are out of the scope of this tutorial. Lets enumerate the available item types:

geopoint
  This is a special field type with no special extraction pattern, and only serves for the purpose of rendering a tuple of 
  latitude/longitude. This type is currently not supported in AS and is only used for rendering purposes in MS, so you can safely ignore 
  this one.

image
  This field type demands the spotted html code to fulfill the format of an image url and renders as an image in the item browser.

number
  The spotted html code must have a number, and only the number part will be extracted.

price
  It is like the number type but will also consider common formatting on price expressions in order to match.

raw html
  The html code as it is. It matches anything except an empty string.

safe html
  Matches any html code which has no empty text content. Also performs some transformation and cleaning over the extracted data in
  order to safely show the html code correctly formatted in a web page.

text
  Matches any html code which has no empty text content, and only extract that text content.

url
  Matches any url expression and renders as a link in the item browser.

Field flags
___________

There are two field flags that modifies the behavior of extraction and item creation:

Required
  When a field is marked as **Required**, it means that the extracted item must contain the field in order to be actually extracted. We 
  will be back to this concept in the next section.

Vary
  AS has a duplicates item detection system, which will reject any item that was already scraped before. In order to accomplish this 
  task, the duplicates detector needs to know which fields it must check in order to effectively find a duplicated item, and all the 
  fields are checked, except those marked  as **Vary**. Lets illustrate with an example, and lets suppose we have an item type with 
  fields *name*, *price*, *description*, *category* and *url*, and *category* and *url* are marked as **Vary**. Lets suppose the AS bot 
  has first scraped the following item:

  * *name*: Louis XIV Table
  * *price*: 1000.00
  * *description*: Very high quality Louis XIV style table
  * *category*: Tables
  * *url*: \http://www.furniture.com/tables/louis-xiv-table.html

  And further, it extracted this item but in a different place in the site:

  * *name*: Louis XIV Table
  * *price*: 1000.00
  * *description*: Very high quality Louis XIV style table
  * *category*: Living Room
  * *url*: \http://www.furniture.com/living-room/louis-xiv-table.html

  It is, of course, the same product, but the specific map of the site makes it appear in two different places, under different 
  product categories. Because *url* and *category* are marked as **Vary**, only *name*, *price* and *description* are checked by the 
  duplicates detector. And as all them has the same value in both items, the second one is considered as a duplicate of the first, and 
  rejected. Observe that if *url* and *category* were not marked as **Vary**, then the duplicates detection system would consider both
  as different products, and so both would be generated. The term "Vary" is used then to indicate that those fields can vary its values 
  but still be the same item.

Shortcut Key
____________

You can add a field shortcut key in order to quickly select a field when you are within the annotation tool. We will go back to field 
shortcuts in more  detail in the section about the annotation tool (still not redacted)

How templates are used in the extraction process
================================================
	
If your spider has only one template, the process is very simple: a scan is performed on every target page using the annotations in the 
template, and if all **Required** fields are extracted, based on a relative positional algorithm and the extraction rules defined by the 
field data type described in previous section, then the complete item is extracted. If some of the fields marked as **Required** was not 
found in the target, then the item is not extracted. And if the item is extracted, it still must pass the duplicates detector check, 
which will decide, as already described, whether the extracted item will be finally accepted or rejected.

If your spider has more than one template, then templates are tried sequentially until the first success extraction occurs. And then, 
duplicates detector is applied over the extracted item, if so. The order in which templates are tried **is not** the same order as they 
were created (as you see them in the panel), but instead, they are sorted by the number of annotations it contains, in decreasing order. 
Only if a subset of templates has the same number of annotations, they will be tried according to age (first created, first). The rule 
to try templates according to number of annotations improves the efficiency of the general extraction algorithm, as the less annotations 
has a template, more easily can be successful in extracting an item by mistake, because there are less constraints to fulfill. So, this 
rule reduces the probability of getting a false positive with the wrong template. Also, the alternatives to handle this kind of false 
positives are easier to implement with this rule, because as template has more annotations, user has the chance to add more constraints.

Spider parameters
=================

When you create a new spider, the minimal attributes you have to fill in order to run the first crawling job, are the spider name and 
the starting URLs (at least one). The first job you will run for a just created spider will be an "annotating" mode job, because the 
obvious first task for every new spider is to add templates. Also, the first (and successive) jobs will give you a better idea about how 
to improve crawling performance by adding url filters. Url filters are optional but in most cases are strongly recommended, as we 
explained in the section about basic concepts.

URL Filters
___________

There is a filter that is always applied, and two kind of custom filters. The always applied filter is the offsite filter. This filter 
avoids the bot to escape from outside the target site/s. Without this kind of filter, the bot would crawl links from the entire web, 
avoiding to focus on our target. And if there weren't other kind of filters, it indeed would crawl all the web.

The offsite filter restricts the bot to only visit links that belong to the web domains in the start URLs, and in the templates (if any) 
URLs, and filters out everything else. It has precedence over any other kind of URL filter. One can ask whether there is not a 
redundancy in adding template URLs domains, as templates were obtained from a crawling limited to the start URLs domains. Well, in most 
cases, this is true, but also can happen that you use one start url for creating templates, and a different one for scraping items, and 
both be from different domains.

The other two kind of URL filters are user custom: **Exclude Pattern** and **Follow Patterns**, bot configurable from the Autoscraping 
Spider properties.

**Exclude Patterns** allow to filter out URL patterns (regular expressions) that must not be visited. You can add as many as you want, one per line in the corresponding widget. **Exclude Patterns** has precedence over **Follow Patterns**.

You can select between 3 modes of link following:

  *. *Follow all links within the spider domains* (except, as already said, those defined in **Excluded Patterns**). Here the spider domains means the ones described above: domains in start URLs and template URLs. As already said, 
  *. *Don't follow links*. Just limit crawling to the starting URLs.
  *. *Follow links that matches the following patterns*. When you select this mode, a new text widget will become visible where you can write the **Follow Patterns** (again, regular expressions) that links has to match in order to be followed.

The **Follow Patterns** are the filters with the less precedence. This fact means that you can't force to follow links on a different 
domain by adding it in this category of filters. The only domains that will be accepted are, as said, those contained in the start URLs 
and those contained in the template URLs.

Despite the simplicity that may seem adding patterns in order to focus only in the desired targets, you must be warned about possible 
unexpected consequences of the usage of URL filters. It is easy to fall in the trap of excluding the visit of pages that you thought you 
didn't need, but when you run a new job the result could be that you also didn't get the ones you do need, because some of the first 
ones contains the links to the second ones, thus cutting the path to them.

Advanced Tools
==============

The tools and procedures described until now are enough in order to solve most cases. However, it is common to have cases for which we 
don't get the expected results. Annotations that extract the wrong region on some targets, templates that are not used for the target 
pages we expected, or data extracted from pages that we don't want to extract anything, are among the most common trouble we may cope 
with. The main source of problems is the fact that the html code layout can present many variations or similarities among different 
target pages, which introduces ambiguities for the extraction algorithm. Also, as we can have multiple templates for the same spider, 
all them intended to be used for different subset of target pages, sometimes it is quite tricky to make the correct template to be 
applied to the correct target (Remember `How templates are used in the extraction process`_). In order to assist on the resolution of 
these problems, some extra constraints has to be imposed to template annotations.

Extra required annotations
==========================

Example 1.
__________

Consider the following case. We have
  * an item type which includes *name*, *price*, *description* and *manufacturer*, where *name* and *price* are required fields, and
  * a template with annotations for all 4 of them

The result in the captured pages are many items correctly scraped (target set A), but many others (target set B) which has no a 
manufacturer but, because of their particular layout, the algorithm matches the item description with the *manufacturer* annotation, 
while the field *description* is not extracted at all because its annotation does not match any similar region in the target. Visually, 
we can roughly illustrate the situation as follow:

layout A: 

+------------+-----------+
|    name    |  -price-  |
+------------+-----------+
|      manufacturer      |
+------------------------+
|      description       |
+------------------------+

layout B:

+------------+-----------+
|    name    |  -price-  |
+------------+-----------+
|      description       |
+------------------------+

So, you add a new template from one of pages of target set B, and annotate *name*, *price* and *description*. You would expect that 
adding this new template, problem will be fixed. But actually is not the case. Because the first template has more annotations than the 
second, it will be tried first. And because it will extract all required data, *name* and *price*, the item will still be created with 
the wrong data, and the second template will never be applied.

You have to add a new constraint. If you open the first template in the annotation tool, you can mark the *description* annotation as 
required. And because in the targets of set B the description is not extracted with this template, then the items will not be created at 
all with it. So the algorithm tries with the second template, which now will correctly extract the three fields.

Observe that, if the templates were not tried in decreasing count of annotations, it may happen that the template with three annotations 
be tried first, and as a result we get wrong extracted data from the pages of set A. In particular, you most probably will get the 
manufacturer data in *description* field, and get missed the real description. But in this case, if there is no other way to 
differentiate among a description and a manufactured data, it is not possible to apply any constraint. In the first approach you can 
constrain the application of the template with four annotations to require to extract the missing field, because with target set A you 
extract four fields, and with target set B you extract three. But in the second approach, the first template tried, the one with three 
annotations, will extract three fields for both sets of targets.

As said before, the more annotations we have, the more constraints we can add.

Example 2.
__________

The less required fields you have, the less constraints you are imposing, and so the most easy you can match wrong targets. As a 
consequence, you not only can match desired targets with wrong template, as in the previous example. But you can also match undesired 
targets which has layout similarities with one or more templates. If you have this problem, a possible approach can be to check whether 
you can mark as required some annotations in the problematic templates, which are not extracted in the undesired targets, and without 
affecting the extraction of desired ones (which still can have those as optional attributes), thus avoiding to create items for them.

But this is not the only approach you can try for this case. May be it is possible to filter out those undesired pages with excluded 
URLs, without affecting the crawling of the site (as mentioned before, could happen that those pages are the ones which contains the 
links to desired pages). This is the most desirable approach in terms of efficiency gain, but not always available. It depends entirely 
on the site particularities and your needs.

Sticky annotations
==================

Another resource that helps to solve some particular problems, is the use of sticky annotations, which are available in the annotation 
tool as "_stickyN" (being N a number) together with the field names. Sticky annotations can be used each time you need additional 
annotations without generating additional extracted data. For example, when you are extracting undesired targets with some of the 
templates, and you don't have the choice to filter by URL or mark some annotations as required, you can still add new annotations in the 
template, that matches particular features of the desired targets that does not exists in the undesired ones: a particular logo, an 
image, a button or a piece of text, for example.

Sticky annotations are implicitly required, and you can add as many ones as you need. Also, consider that by adding more annotations, 
the template may increase its precedence in the templates try sequence.

Template Extractors
===================

Consider the following situation. You have a set of target pages which consists on user profiles, containing tabulated data of the same type: *name*, *gender*, *occupation*, *country*, *favorite books* and *favorite movies*. But, except the page we chosen for template:

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

fields are not required to appear in all the user profiles. This condition will make a mere positional matching to fail, and you will 
have mixed data as result. For example, if a user did not provide the *occupation* and *country*, you will get the favorite books in the 
*occupation* field, the favorite movies in the *country* field, and nothing in the fields *favorite books* and *favorite movies*.

You can't mark as required any of the annotation because actually all them are optional (and also would not solve the positional problem 
anyway)

Here the template extractors come to help, by adding pattern constraints to the template annotations. First, you must annotate, instead 
of the field value cell ("Olive", "Female", etc) the entire field row ("Name: Olive", "Gender: Female", and so on). Then, in the 
template properties, add Regular Expression extractors for each field, in the form:

+------------+------------------+-------------------+
| Field name |       Type       |   Specification   |
+============+==================+===================+
|  *name*    |Regular expression|   Name:\s+(.*?)   |
+------------+------------------+-------------------+
| *gender*   |Regular expression|  Gender:\s+(.*?)  |
+------------+------------------+-------------------+
|*occupation*|Regular expression|Occupation:\s*(.*?)|
+------------+------------------+-------------------+
|    ...     |       ...        |        ...        |
+------------+------------------+-------------------+

And so on.

When you choose a Regular expression extractor, the specification must consist on a regular expression pattern that must match the 
extracted data for the corresponding field. If the extracted data does not match the pattern, then the field is not extracted. If the 
extracted data does match the pattern, then it is replaced by the match group enclosed between parenthesis (or a concatenation of all 
them, if more than one group given). This way, you will ensure that correct annotation match the correct target row, and you will only 
extract the part that you are interested in.

Of course, this tactic will be useful only if you can annotate a region that has some key word or repeated pattern, and all be different for each field.

Autoscraping and ScrapingHub API
================================

If you want to manage AS job scheduling using the ScrapingHub :doc:`api`, AS bot supports to pass start_urls as a list of URLs separated by new lines. This feature is very useful for passing a list of URLs from a text file.

For example, if you have all your start URLs in a file named start_urls.txt, one per line, you can do, from a linux console::

    curl http://panel.scrapinghub.com/api/schedule.json -d project=155 -d spider=myspider -u <your api key>: -d start_urls="$(cat start_urls.txt)"

or, using `scrapinghub python api <https://github.com/scrapinghub/python-scrapinghub>`_::

    >>> from scrapinghub import Connection
    >>> conn = Connection('<your api key>')
    >>> project = conn["155"]
    >>> project.schedule("myspider", start_urls=open("start_urls.txt").read())


