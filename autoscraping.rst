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

    #. Create the spider and configure (minimal configuration is the name of the spider and the starting url)
    #. Run in annotating mode
    #. Go to captured pages and find one from which you need scraped data
    #. Add a template, and annotate it
    #. Go back to captured pages and check how items are extracted from them
    #. Eventually improve current template and/or jump to step 4.
    #. Run in normal mode and get the items

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
early stop of job as result of an unaccomplished item/hour rate threshold.

This section has been a basic, but very important, overview of general concepts that you must know in order to better understand
the detailed description that will come on following sections.

