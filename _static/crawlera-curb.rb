require 'curb'

url = "http://twitter.com"
proxy = "proxy.crawlera.com:8010"
proxy_auth = "<API KEY>:"

c = Curl::Easy.new(url) do |curl|
  curl.headers["X-Crawlera-Use-HTTPS"] = 1
  curl.proxypwd = proxy_auth
  curl.proxy_url = proxy
  curl.verbose = true
end

c.perform
puts c.body_str
