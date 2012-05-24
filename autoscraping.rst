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

How templates are used in the extraction process
================================================

If your spider has only one template, the process is very simple: a scan is performed on every target page using the annotations in the 
template, and if all **Required** fields are extracted, based on a relative positional algorithm and the extraction rules defined by the 
field data type described in previous section, then the complete item is extracted. If some of the fields marked as **Required** was not 
found in the target, then the item is not extracted. And if the item is extracted, it still must pass the duplicates detector check, 
which will decide, as already described, if the extracted item will be finally accepted or rejected.

If your spider has more than one template, then templates are tried sequentially until the first success extraction occurs. And then, 
duplicates detector is applied over the extracted item, if so. The order in which templates are tried **is not** the same order as they 
were created (as you see them in the panel), but instead, they are sorted by the number of annotations it contains, in decreasing order. 
Only if a subset of templates has the same number of annotations, they will be tried according to age (first created, first). The rule 
to try templates according to number of annotations improves the efficiency of the general extraction algorithm, as the less annotations 
has a template, more easily can be successful in extracting an item by mistake, because there are less constraints to fulfill. So, this 
rule reduces the probability of getting a false positive with the wrong template. Also, the alternatives to handle this kind of false 
positives are easier to implement with this rule, because as template has more annotations, it has the chance to add more constraints.


