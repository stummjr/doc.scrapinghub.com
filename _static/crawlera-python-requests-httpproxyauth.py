import requests
from requests.auth import HTTPProxyAuth

url = "https://twitter.com"
headers = {}
proxy_host = "proxy.crawlera.com"
proxy_auth = HTTPProxyAuth("<API KEY>", "")
proxies = {"http": "http://{}:8010/".format(proxy_host)}

if url.startswith("https:"):
    url = "http://" + url[8:]
    headers["X-Crawlera-Use-HTTPS"] = "1"

r = requests.get(url, headers=headers, proxies=proxies, auth=proxy_auth)

print("""
Requesting [{}]
through proxy [{}]

Response Time: {}
Response Code: {}
Response Headers:
{}

Response Body:
{}
""".format(url, proxy_host, r.elapsed.total_seconds(), r.status_code, 
           r.headers, r.text))
