require 'rubygems'
require 'mechanize'

url = "https://twitter.com"
proxy_host = "proxy.crawlera.com"
proxy_api_key = "<API KEY>"

agent = Mechanize.new
agent.set_proxy proxy_host, 8010, proxy_api_key, ''

res = agent.get(url)
puts res.body
