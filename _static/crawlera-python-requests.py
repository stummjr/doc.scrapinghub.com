import requests

url = "http://twitter.com"
proxy = "paygo.crawlera.com:8010"
proxy_auth = "USER:PASS"

proxies = {
    "http": "http://{0}@{1}/".format(proxy_auth, proxy)
}

headers = {
    "X-Crawlera-Use-HTTPS": 1
}

r = requests.get(url, proxies=proxies, headers=headers)

print "\nRequesting    [", url, "]"
print "through proxy [", proxy, "]"
print "\n\nResponse Time:", r.elapsed.total_seconds()
print "\n\nResponse Code:", r.status_code
print "\n\nResponse Headers:\n"
print r.headers
print "\n\nResponse Body:\n"
print r.text
