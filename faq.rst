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
