.. _portia:

======
Portia
======

Portia let's you create spiders through a visual interface and then export them to Scrapy Cloud. This page mostly covers using Portia with Dash, for information on how Portia itself works take a look at the `documentation <https://github.com/scrapinghub/portia>`_.

Creating a Portia Project
=========================

You can create a new Portia project from Dash's homepage. When you create a new project, you will be redirected to Portia and can begin creating spiders. Existing spiders can be accessed on the right.

If you want to create Portia spiders within an existing project, you can enable Portia in the project's Addons section. You can then access Portia by clicking the Portia tab on the left.

Spiders, Templates and Items
============================

You can create a spider from the project context by entering the URL in Portia's address bar and clicking 'New spider'. Portia will render the page and set the spider's URL to the one you entered.

On the right you'll find the spider settings toolbox. Here you can define your spider's crawling behaviour, start URLs, and login information if required by the target website.

When you first annotate a page, a template will be created. Templates are used to model a web page and define which data you want to extract from it. Templates are made up of annotations, which define a mapping between an element on the page and the extracted item's field. 

.. image:: _static/portia-create-annotation.png
    :width: 300px

You can create an annotation by clicking on an element, and then map the element's content or one of its attributes to a field of the item you're wanting to extract.

The extracted item type can be set on the right hand side, under 'Extracted item type'. Here you can also modify the item's fields if necessary.

.. image:: _static/portia-edit-item.png
    :width: 300px

An item refers to a single item of data scraped from the target website, such as a product, job listing etc. In Portia, items are extracted per template, so it's possible to scrape multiple item types if you use a different template for each one.

Even if you're only extracting a single item, in many cases you will need to use more than one template. This is because a lot of websites use different layouts depending on the content being displayed, and in some cases the layout may differ between items you're extracting.

Extractors allow you to further refine field values extracted from a page using regexes or a predefined type. This is useful if you only want a subselection of an element's content or attribute.

Publishing to Dash
==================

Once your have tested your spiders, you're ready to publish your project to Dash. Click the project name in the breadcrumbs on the top left to enter the project context. From here you can click the 'Publish changes' button found in the toolbox menu on the right.

When you publish your project, if successful you will be asked if you want to be redirected to the schedule page. Click 'OK' and you will be taken to the spiders page in Dash, here you can schedule your spider by clicking the 'Schedule' button located at the top right.

.. image:: _static/portia-scraped-items.png
    :width: 600px

Portia spiders run just like any Scrapy spider, and you can view the results when the job is complete.

