from scrapy.spider import Spider
from scrapy.selector import Selector
from scrapy.http import Request
from scrapy.item import Item, Field
from scrapy.contrib.loader import ItemLoader
from scrapy.contrib.loader.processor import MapCompose, TakeFirst
from urlparse import urljoin

class Company(Item):
    name = Field()
    logo = Field()
    website = Field()


class CompanyLoader(ItemLoader):
    default_input_processor = MapCompose(unicode.strip)
    default_output_processor = TakeFirst()


class CrunchbaseSpider(Spider):
    name = "crunchbase"
    allowed_domains = ["crunchbase.com"]
    start_urls = ['http://www.crunchbase.com/companies?q=pets']

    def parse(self, response):
        s = Selector(response)
        for x in s.xpath("//table[@class='col2_table_listing']//a/@href").extract():
            yield Request(urljoin(response.url, x), self.parse_company)

    def parse_company(self, response):
        l = CompanyLoader(item=Company(), response=response)
        l.add_xpath("logo", "//div[@id='company_logo']//img/@src")
        l.add_xpath("name", "//h1[@class='h1_first']/text()")
        l.add_xpath("website", "(//td[@class='td_right']/a/@href)[1]")
        return l.load_item()
