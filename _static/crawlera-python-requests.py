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

print("""
Requesting [{}]
troguh proxy [{}]

Response Time: {}
Response Code: {}
Response Headers:
{}

Response Body:
{}
""".format(url, proxy, r.elapsed.total_seconds(), r.status_code, r.headers, r.text))
