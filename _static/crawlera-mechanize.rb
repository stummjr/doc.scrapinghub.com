require 'rubygems'
require 'mechanize'

url = "http://twitter.com"
proxy_host = "proxy.crawlera.com"
proxy_api_key = "<API KEY>"

agent = Mechanize.new
agent.set_proxy proxy_host, 8010, proxy_api_key, ''
agent.request_headers = {"X-Crawlera-Use-HTTPS" => 1}

res = agent.get(url)
puts res.body
